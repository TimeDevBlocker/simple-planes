package xyz.przemyk.simpleplanes.upgrades.shooter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import xyz.przemyk.simpleplanes.SimplePlanesMod;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;
import xyz.przemyk.simpleplanes.setup.SimplePlanesItems;
import xyz.przemyk.simpleplanes.setup.SimplePlanesUpgrades;
import xyz.przemyk.simpleplanes.upgrades.Upgrade;

import javax.annotation.Nullable;
import java.util.Random;

import static net.minecraft.item.Items.FIREWORK_ROCKET;
import static net.minecraft.item.Items.FIRE_CHARGE;

public class ShooterUpgrade extends Upgrade {
    public static final ResourceLocation TEXTURE = new ResourceLocation(SimplePlanesMod.MODID, "textures/plane_upgrades/shooter.png");

    private boolean shootSide = false;

    public ShooterUpgrade(PlaneEntity planeEntity) {
        super(SimplePlanesUpgrades.SHOOTER.get(), planeEntity);
    }

    @Override
    public void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        PlayerEntity player = event.getPlayer();
        ItemStack itemStack = player.getHeldItem(event.getHand());
        Vector3f motion1 = planeEntity.transformPos(new Vector3f(0, 0, (float) (1 + planeEntity.getMotion().length())));
        Vector3d motion = new Vector3d(motion1);
        World world = event.getWorld();
        Random random = world.rand;

        Vector3f pos = planeEntity.transformPos(new Vector3f(shootSide ? 0.8f : -0.8f, 0.8f, 0.8f));
        shootSide = !shootSide;
        updateClient();

        double x = pos.getX() + planeEntity.getPosX();
        double y = pos.getY() + planeEntity.getPosY();
        double z = pos.getZ() + planeEntity.getPosZ();

        Item item = itemStack.getItem();

        if (item == FIREWORK_ROCKET) {
            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(world, itemStack, x, y, z, true);
            fireworkrocketentity.shoot(-motion.x, -motion.y, -motion.z, -(float) Math.max(0.5F, motion.length() * 1.5), 1.0F);
            world.addEntity(fireworkrocketentity);
            if (!player.isCreative()) {
                itemStack.shrink(1);
            }
        } else if (item == FIRE_CHARGE) {
            double d3 = random.nextGaussian() * 0.05D + 2 * motion.x;
            double d4 = random.nextGaussian() * 0.05D;
            double d5 = random.nextGaussian() * 0.05D + 2 * motion.z;
            AbstractFireballEntity fireBallEntity = Util
                .make(new SmallFireballEntity(world, player, d3, d4, d5), (p_229425_1_) -> p_229425_1_.setStack(itemStack));
            fireBallEntity.forceSetPosition(x, y, z);
            fireBallEntity.setMotion(motion.scale(2));
            world.addEntity(fireBallEntity);
            if (!player.isCreative()) {
                itemStack.shrink(1);
            }
        } else if (item == Items.ARROW) {
            ArrowEntity arrowentity = new ArrowEntity(world, x, y, z);
            arrowentity.setShooter(player);
            arrowentity.setMotion(motion.scale(Math.max(motion.length() * 1.5, 3) / motion.length()));
            if (!player.isCreative()) {
                itemStack.shrink(1);
                arrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
            }
            world.addEntity(arrowentity);
        } else if (item == Items.TIPPED_ARROW) {
            ArrowEntity arrowEntity = new ArrowEntity(world, x, y, z);
            arrowEntity.setShooter(player);
            arrowEntity.setPotionEffect(itemStack);
            arrowEntity.setMotion(motion.scale(Math.max(motion.length() * 1.5, 3) / motion.length()));
            if (!player.isCreative()) {
                itemStack.shrink(1);
                arrowEntity.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
            }
            world.addEntity(arrowEntity);
        } else if (item == Items.SPECTRAL_ARROW) {
            SpectralArrowEntity arrowEntity = new SpectralArrowEntity(world, x, y, z);
            arrowEntity.setShooter(player);
            arrowEntity.setMotion(motion.scale(Math.max(motion.length() * 1.5, 3) / motion.length()));
            if (!player.isCreative()) {
                itemStack.shrink(1);
                arrowEntity.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
            }
            world.addEntity(arrowEntity);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, float partialTicks) {
        IVertexBuilder ivertexbuilder = buffer.getBuffer(ShooterModel.INSTANCE.getRenderType(TEXTURE));
        ShooterModel.INSTANCE.render(matrixStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void writePacket(PacketBuffer buffer) {
        buffer.writeBoolean(shootSide);
    }

    @Override
    public void readPacket(PacketBuffer buffer) {
        shootSide = buffer.readBoolean();
    }

    @Override
    public void dropItems() {
        planeEntity.entityDropItem(SimplePlanesItems.SHOOTER.get());
    }
}
