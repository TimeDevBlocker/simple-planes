package xyz.przemyk.simpleplanes.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;
import xyz.przemyk.simpleplanes.MathUtil;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;
import xyz.przemyk.simpleplanes.upgrades.Upgrade;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static xyz.przemyk.simpleplanes.client.render.FurnacePlaneModel.TICKS_PER_PROPELLER_ROTATION;

public abstract class AbstractPlaneRenderer<T extends PlaneEntity> extends EntityRenderer<T> {
    protected EntityModel<PlaneEntity> propellerModel;
    public static final ResourceLocation PROPELLER_TEXTURE = new ResourceLocation("textures/block/iron_block.png");

    protected AbstractPlaneRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        propellerModel = new PropellerModel();
    }

    public static float getPropellerRotation(PlaneEntity entity, float partialTicks) {
        return ((entity.ticksExisted + partialTicks) % TICKS_PER_PROPELLER_ROTATION) / (float) (TICKS_PER_PROPELLER_ROTATION / 10.0f * Math.PI);
    }

    @Override
    public Vector3d getRenderOffset(T entityIn, float partialTicks) {
        if (Minecraft.getInstance().player != null) {
            ClientPlayerEntity playerEntity = Minecraft.getInstance().player;
            if (playerEntity == entityIn.getControllingPassenger()) {
                if ((Minecraft.getInstance()).gameSettings.pointOfView == PointOfView.FIRST_PERSON) {

                    return new Vector3d(0, 0, 0);
                }
            }
        }

        return super.getRenderOffset(entityIn, partialTicks);
    }

    @Override
    public void render(T planeEntity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.translate(0.0D, 0.375D, 0.0D);
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        matrixStackIn.translate(0, -0.5, 0);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180));

        double firstPersonYOffset = -0.7D;
        //        boolean fpv = Minecraft.getInstance().player != null && Minecraft.getInstance().player == planeEntity.getControllingPassenger() && (Minecraft.getInstance()).gameSettings.thirdPersonView == 0;
        boolean isPlayerRidingInFirstPersonView = Minecraft.getInstance().player != null && planeEntity.isPassenger(Minecraft.getInstance().player)
            && (Minecraft.getInstance()).gameSettings.pointOfView == PointOfView.FIRST_PERSON;
        if (isPlayerRidingInFirstPersonView) {
            matrixStackIn.translate(0.0D, firstPersonYOffset, 0.0D);
        }
        Quaternion q = MathUtil.lerpQ(partialTicks, planeEntity.getQ_Prev(), planeEntity.getQ_Client());
        matrixStackIn.rotate(q);

        float rockingAngle = planeEntity.getRockingAngle(partialTicks);
        if (!MathHelper.epsilonEquals(rockingAngle, 0.0F)) {
            matrixStackIn.rotate(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), rockingAngle, true));
        }
        float f = (float) planeEntity.getTimeSinceHit() - partialTicks;
        float f1 = planeEntity.getDamageTaken() - partialTicks;
        if (f1 < 0.1F) {
            f1 = 0.1F;
        }

        if (f > 0.0F) {
            float angle = MathHelper.clamp(f * f1 / 200.0F, -30, 30);
//            float angle = 30;
            f = planeEntity.ticksExisted + partialTicks;
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.sin(f) * angle));
        }

        matrixStackIn.translate(0, -0.6, 0);

        if (isPlayerRidingInFirstPersonView) {
            matrixStackIn.translate(0.0D, -firstPersonYOffset, 0.0D);
        }

        EntityModel<T> planeModel = getModel();
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(planeModel.getRenderType(this.getEntityTexture(planeEntity)));
        planeModel.setRotationAngles(planeEntity, partialTicks, 0, 0, 0, 0);
        planeModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        int seat = 0;
        for (Upgrade upgrade : planeEntity.upgrades.values()) {
//            matrixStackIn.push();
//            if (upgrade.getType().occupyBackSeat) {
//                for (int i = 0; i < upgrade.getSeats(); i++) {
//                    matrixStackIn.push();
//                    BackSeatBlockModel.moveMatrix(planeEntity, matrixStackIn, seat);
//                    upgrade.render(matrixStackIn, bufferIn, packedLightIn, partialTicks);
//                    seat++;
//                    matrixStackIn.pop();
//                }
//            } else {
                upgrade.render(matrixStackIn, bufferIn, packedLightIn, partialTicks);
//            }
//            matrixStackIn.pop();
        }

        ivertexbuilder = ItemRenderer.getArmorVertexBuilder(bufferIn, planeModel.getRenderType(PROPELLER_TEXTURE), false, planeEntity.hasNoGravity());

        propellerModel.setRotationAngles(planeEntity, partialTicks, 0, 0, 0, 0);
        propellerModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.push();
        renderAdditional(planeEntity, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
        matrixStackIn.pop();

        super.render(planeEntity, 0, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    protected void renderAdditional(T planeEntity, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {}

    protected abstract EntityModel<T> getModel();

    @Override
    public ResourceLocation getEntityTexture(PlaneEntity entity) {
        Block block = entity.getMaterial();
        if (cachedTextures.containsKey(block)) {
            return cachedTextures.get(block);
        }

        ResourceLocation texture;
        try {
            Random random = new Random(42L);
            ResourceLocation sprite = Minecraft.getInstance().getModelManager().getModel(ModelLoader.getInventoryVariant(block.getRegistryName().toString())).getQuads(null, Direction.SOUTH, random, EmptyModelData.INSTANCE).get(0).getSprite().getName();
            texture = new ResourceLocation(sprite.getNamespace(), "textures/" + sprite.getPath() + ".png");
        } catch (IndexOutOfBoundsException exception) {
            texture = FALLBACK_TEXTURE;
        }
        cachedTextures.put(block, texture);
        return texture;
    }

    public static final Map<Block, ResourceLocation> cachedTextures = new HashMap<>();
    public static final ResourceLocation FALLBACK_TEXTURE = new ResourceLocation("minecraft", "textures/block/oak_planks.png");
}