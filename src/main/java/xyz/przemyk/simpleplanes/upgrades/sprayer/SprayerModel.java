package xyz.przemyk.simpleplanes.upgrades.sprayer;
// Made with Blockbench 3.5.2
// Exported for Minecraft version 1.15

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;

@SuppressWarnings("FieldCanBeLocal")
public class SprayerModel extends EntityModel<PlaneEntity> {
    public static final SprayerModel INSTANCE = new SprayerModel();

    private final ModelPart Body;
    private final ModelPart sprayer;
    private final ModelPart spray_left;
    private final ModelPart spray_right;

    public SprayerModel() {
        textureWidth = 256;
        textureWidth = 64;
        textureHeight = 64;

        Body = new ModelPart(this);
        Body.setPivot(0.0F, 17.0F, 0.0F);
        setRotationAngle(Body, 0.0F, 0.0F, 0.0F);

        sprayer = new ModelPart(this);
        sprayer.setPivot(0.0F, 7.0F, 0.0F);
        Body.addChild(sprayer);

        spray_left = new ModelPart(this);
        spray_left.setPivot(0.0F, -7.0F, 0.0F);
        sprayer.addChild(spray_left);
        setRotationAngle(spray_left, 0.0F, 0.0F, -0.0873F);
        spray_left.setTextureOffset(9, 11).addCuboid(13.0F, 0.0F, -12.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
        spray_left.setTextureOffset(0, 8).addCuboid(28.0F, 0.0F, -12.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
        spray_left.setTextureOffset(0, 4).addCuboid(8.0F, -2.0F, -12.0F, 23.0F, 1.0F, 3.0F, 0.0F, false);

        spray_right = new ModelPart(this);
        spray_right.setPivot(0.0F, -7.0F, 0.0F);
        sprayer.addChild(spray_right);
        setRotationAngle(spray_right, 0.0F, 0.0F, 0.0873F);
        spray_right.setTextureOffset(9, 17).addCuboid(-31.0F, 0.0F, -12.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
        spray_right.setTextureOffset(0, 0).addCuboid(-31.0F, -2.0F, -12.0F, 23.0F, 1.0F, 3.0F, 0.0F, false);
        spray_right.setTextureOffset(0, 14).addCuboid(-16.0F, 0.0F, -12.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
    }

    @Override
    public void setAngles(PlaneEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        //previously the render function, render code was moved to a method below
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Body.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.pitch = x;
        modelRenderer.yaw = y;
        modelRenderer.roll = z;
    }
}