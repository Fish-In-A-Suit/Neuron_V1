package neuron.android.com.neuron.core;

/**
 * Manages the terminated snapshots
 */
public class TerminatedSnapshotManager {
    private static SecondarySignupTerminatedSnapshot secondarySignupTerminatedSnapshot;

    public static void setSecondarySignupTerminatedSnapshot(SecondarySignupTerminatedSnapshot ssts) {
        secondarySignupTerminatedSnapshot = ssts;
    }

    public static SecondarySignupTerminatedSnapshot getSecondarySignupTerminatedSnapshot() {
        return secondarySignupTerminatedSnapshot;
    }
}
