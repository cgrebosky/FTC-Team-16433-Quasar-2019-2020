package quasar.lib.macro;

public interface MacroSystem {
    /**
     * This should set the subsystem to the current state
     */
    public void setMacroState();
    public void getMacroState();
}
