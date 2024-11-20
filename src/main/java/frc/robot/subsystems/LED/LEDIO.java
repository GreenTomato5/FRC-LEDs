package frc.robot.subsystems.LED;

public interface LEDIO {
    // @AutoLog
    public class LEDIOInputs {
        public int pwmSetSpeed;
        public double pwmSpeed;
    }

    default void updateInputs(LEDIOInputs inputs) {}

    /**
     * Set PWM state in micro seconds update thing
     * @param signal
     */
    default void setPWMState(int signal) {}
}
