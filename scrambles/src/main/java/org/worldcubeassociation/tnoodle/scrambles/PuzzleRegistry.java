package org.worldcubeassociation.tnoodle.scrambles;

import org.worldcubeassociation.tnoodle.puzzle.*;


import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.nativeimage.c.type.CCharPointer;
import com.oracle.svm.core.c.CConst;
// GraalVM 23.0 +: import org.graalvm.nativeimage.c.type.CConst;


public enum PuzzleRegistry {
    TWO(TwoByTwoCubePuzzle.class),
    THREE(ThreeByThreeCubePuzzle.class),
    FOUR(FourByFourCubePuzzle.class),
    FOUR_FAST(FourByFourRandomTurnsCubePuzzle.class),
    FIVE(CubePuzzle.class, 5),
    SIX(CubePuzzle.class, 6),
    SEVEN(CubePuzzle.class, 7),
    THREE_NI(NoInspectionThreeByThreeCubePuzzle.class),
    FOUR_NI(NoInspectionFourByFourCubePuzzle.class),
    FIVE_NI(NoInspectionFiveByFiveCubePuzzle.class),
    THREE_FM(ThreeByThreeCubeFewestMovesPuzzle.class),
    PYRA(PyraminxPuzzle.class),
    SQ1(SquareOnePuzzle.class),
    MEGA(MegaminxPuzzle.class),
    CLOCK(ClockPuzzle.class),
    SKEWB(SkewbPuzzle.class);

    private final LazySupplier<? extends Puzzle> puzzleSupplier;

    <T extends Puzzle> PuzzleRegistry(Class<T> suppliyingClass, Object... ctorArgs) {
        this.puzzleSupplier = new LazySupplier<>(suppliyingClass, ctorArgs);
    }

    public Puzzle getScrambler() {
        return this.puzzleSupplier.getInstance();
    }

    // WORD OF ADVICE: The puzzles that use local scrambling mechanisms
    // should not take long to boot anyways because their computation-heavy
    // code is wrapped in ThreadLocal objects that are only executed on-demand

    public String getKey() {
        return this.getScrambler().getShortName();
    }

    public String getDescription() {
        return this.getScrambler().getLongName();
    }
}

class Main {
    private static final Puzzle[] puzzles = {
        new TwoByTwoCubePuzzle(),
        new ThreeByThreeCubePuzzle(),
        new FourByFourCubePuzzle(),
        new CubePuzzle(5),
        new CubePuzzle(6),
        new CubePuzzle(7),
        new SquareOnePuzzle(),
        new MegaminxPuzzle(),
        new PyraminxPuzzle(),
        new ClockPuzzle(),
        new SkewbPuzzle(),
        new ThreeByThreeCubePuzzle(),
        new NoInspectionThreeByThreeCubePuzzle(),
        new NoInspectionFourByFourCubePuzzle(),
        new NoInspectionFiveByFiveCubePuzzle(),
    };

    // TODO Colorscheme - static non-final with setter and nice default.


    @CEntryPoint(name = "tnoodle_lib_scramble") public static CCharPointer scramble(IsolateThread thread, int id) {
        String s = puzzles[id].generateScramble();
        return CTypeConversion.toCString(s).get();
    }

    @CEntryPoint(name = "tnoodle_lib_draw_scramble") public static CCharPointer drawScramble(IsolateThread thread, int id, @CConst CCharPointer scramble) {
        String scr = CTypeConversion.toJavaString(scramble);
        String svg;
        try {
            svg = puzzles[id].drawScramble(scr, null).toString();
        } catch (InvalidScrambleException e) {
            svg = null;
        }
        return CTypeConversion.toCString(svg).get();
    }


    public static void main(String[] args) {}
}
