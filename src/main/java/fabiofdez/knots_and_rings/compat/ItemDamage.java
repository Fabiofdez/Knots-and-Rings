package fabiofdez.knots_and_rings.compat;

import net.minecraft.world.InteractionHand;
//? > 1.21 && < 1.21.11
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemDamage {

  public static void hurtAndBreak(ItemStack stack, InteractionHand hand, Player player, int damage) {
    stack.hurtAndBreak(
        damage, player,
        //? if < 1.21 {
        /*(p) -> p.broadcastBreakEvent(hand)
        *///? } else if < 1.21.11 {
        LivingEntity.getSlotForHand(hand)
        //? } else {
         /*hand.asEquipmentSlot()
        *///? }
    );
  }
}
