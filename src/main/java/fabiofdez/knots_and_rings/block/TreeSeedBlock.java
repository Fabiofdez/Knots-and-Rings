package fabiofdez.knots_and_rings.block;

import fabiofdez.knots_and_rings.block.state.BlockPosOffset;
import fabiofdez.knots_and_rings.feature.GrowingSapling;
import fabiofdez.knots_and_rings.feature.SaplingType;
import fabiofdez.knots_and_rings.mixin.VegetationBlockAccessor;
import fabiofdez.knots_and_rings.util.ShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

//? > 1.21 {
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//? }

public class TreeSeedBlock extends /*? if <= 1.21.1 { *//*BushBlock*//*? } else { */VegetationBlock/*? } */ implements BonemealableBlock {

  private static final List<BlockPosOffset> NEIGHBOR_OFFSETS = BlockPosOffset.MemberSet.ALL_AROUND.stream().toList();
  private static final VoxelShape SHAPE = ShapeUtil.column(12, 5);

  protected final SaplingBlock saplingBlock;

  //? > 1.21 {
  public static MapCodec<TreeSeedBlock> CODEC = RecordCodecBuilder.mapCodec((inst) -> inst
      .group(SaplingBlock.CODEC.fieldOf("sapling").forGetter((seed) -> seed.saplingBlock), propertiesCodec())
      .apply(inst, TreeSeedBlock::new));

  @NotNull
  @Override
  protected MapCodec<? extends /*? if <= 1.21.1 { *//*BushBlock*//*? } else { */ VegetationBlock/*? } */> codec() {
    return CODEC;
  }
  //? }

  public TreeSeedBlock(Block saplingBlock, Properties properties) {
    super(properties);
    this.saplingBlock = (SaplingBlock) saplingBlock;
  }

  @Override
  protected boolean mayPlaceOn(BlockState state, BlockGetter blockGetter, BlockPos pos) {
    return ((VegetationBlockAccessor) this.saplingBlock).knots_and_rings$mayPlaceOn(saplingState(), blockGetter, pos);
  }

  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    return ((VegetationBlockAccessor) this.saplingBlock).knots_and_rings$canSurvive(saplingState(), level, pos);
  }

  @Override
  protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (random.nextInt(3) != 0) return;
    this.advanceSapling(level, pos);
    this.advanceNearby(level, pos, random);
  }

  @NotNull
  @Override
  protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state/*? if < 1.21 { *//*, boolean b*//*? } */) {
    return this.saplingBlock.isValidBonemealTarget(level, pos, saplingState()/*? if < 1.21 { *//*, b*//*? } */);
  }

  @Override
  public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
    return this.saplingBlock.isBonemealSuccess(level, random, pos, saplingState());
  }

  @Override
  public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
    this.advanceSapling(level, pos);
    this.advanceNearby(level, pos, random);
  }

  private BlockState saplingState() {
    return this.saplingBlock.defaultBlockState();
  }

  private void advanceSapling(ServerLevel level, BlockPos pos) {
    BlockState state = SaplingType.ofSeed(this).placedSapling(level, pos);
    if (GrowingSapling.isGrowingSapling(state)) {
      state = state.setValue(GrowingSapling.Properties.GROWTH_STAGE, GrowingSapling.Stage.SPROUT);
    }

    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 16);
    level.setBlockAndUpdate(pos, state);
  }

  private void advanceNearby(ServerLevel level, BlockPos pos, RandomSource random) {
    if (random.nextInt(3) == 0) return;

    List<BlockPos> neighborSeedlings = NEIGHBOR_OFFSETS
        .stream()
        .map((offset) -> offset.from(pos))
        .filter((neighborPos) -> level.getBlockState(neighborPos).is(this))
        .toList();

    if (neighborSeedlings.isEmpty()) return;

    int neighborIdx = random.nextInt(neighborSeedlings.size());
    BlockPos neighborPos = neighborSeedlings.get(neighborIdx);

    this.advanceSapling(level, neighborPos);
  }

  @NotNull
  @Override
  protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
    return List.of(this.asItem().getDefaultInstance());
  }
}
