package basicmod.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonJson;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.Watcher;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import javassist.CtBehavior;

import static basicmod.BasicMod.characterScale;
import static basicmod.BasicMod.enemyScale;

public class RenderPatch {
    @SpirePatches({
            @SpirePatch(clz = AbstractPlayer.class, method = "renderPlayerImage"),
            @SpirePatch(clz = Watcher.class, method = "renderPlayerImage")
    })
    public static class PlayerScalePatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(AbstractPlayer __instance, SpriteBatch sb, Skeleton ___skeleton) {
            if (___skeleton != null) {
                Bone root = ___skeleton.getRootBone();
                root.setScaleX(root.getScaleX() * characterScale);
                root.setScaleY(root.getScaleY() * characterScale);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Skeleton.class, "updateWorldTransform");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractCreature.class,
            method = "loadAnimation"
    )
    public static class EnemyScalePatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(AbstractCreature __instance, String atlasUrl, String skeletonUrl, @ByRef float[] scale) {
            if (!__instance.isPlayer) {
                scale[0] /= enemyScale;
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {

                Matcher finalMatcher = new Matcher.MethodCallMatcher(
                        SkeletonJson.class, "setScale"
                );
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
