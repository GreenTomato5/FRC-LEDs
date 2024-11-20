package frc.robot.pioneersLib.CI;

import java.util.concurrent.atomic.AtomicReference;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.hal.DriverStationJNI;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import frc.robot.Robot;

public class CrashCheck extends RobotBase {
    private static final AtomicReference<CrashCheck> instance = new AtomicReference<>();
    private final Robot robot;
    private final AtomicReference<CrashCheckStates> currentState = new AtomicReference<>(CrashCheckStates.DISABLED);
    private final AtomicReference<CrashCheckStates> lastState = new AtomicReference<>(CrashCheckStates.TEST);
    
    private long startTime = 0;
    // Holy I hate this notation so much (Required as a flag)
    private boolean m_robotMainOverridden;
    private boolean lastStateSet = false;

    private enum CrashCheckStates {
        DISABLED("DISABLED", () -> {
            getInstance().getRobot().robotPeriodic();
            getInstance().getRobot().disabledPeriodic();
        }, () -> {
            getInstance().getRobot().disabledInit();
        }, () -> {
            getInstance().getRobot().disabledExit();
        }),
        TELEOP("TELEOP", () -> {
            getInstance().getRobot().robotPeriodic();
            getInstance().getRobot().teleopPeriodic();
        }, () -> {
            getInstance().getRobot().teleopInit();
        }, () -> {
            getInstance().getRobot().teleopExit();
        }),
        AUTONOMOUS("AUTONOMOUS", () -> {
            getInstance().getRobot().robotPeriodic();
            getInstance().getRobot().autonomousPeriodic();
        }, () -> {
            getInstance().getRobot().autonomousInit();
        }, () -> {
            getInstance().getRobot().autonomousExit();
        }),
        TEST("TEST", () -> {
            getInstance().getRobot().robotPeriodic();
            getInstance().getRobot().testPeriodic();
        }, () -> {
            getInstance().getRobot().testInit();
        }, () -> {
            getInstance().getRobot().testExit();
        });

        private String state;
        private Runnable periodic;
        private Runnable init;
        private Runnable exit;

        CrashCheckStates(String state, Runnable periodic, Runnable init, Runnable exit) {
            this.state = state;
            this.periodic = periodic;
            this.init = init;
            this.exit = exit;
        }

        public String getStateString() {
            return state;
        }

        public void periodic() {
            periodic.run();
        }

        public void init() {
            init.run();
        }

        public void exit() {
            exit.run();
        }
    }

    public CrashCheck() {
        this.robot = new Robot();
        HAL.initialize(500, 0);

        DriverStationSim.setEnabled(false);
        DriverStationSim.setAutonomous(false);
        DriverStationSim.setTest(false);

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.exit(1);
        });
    }

    public Robot getRobot() {
        return robot;
    }

    public static CrashCheck getInstance() {
        synchronized (CrashCheck.class) {
            if (instance.get() == null) {
                CrashCheck newInstance = new CrashCheck();
                instance.set(newInstance);
                return newInstance;
            }
            return instance.get();
        }
    }

    private void updateState() {
        if (lastState.get() != currentState.get() && lastStateSet) {
            if (lastState != null) {
                lastState.get().exit();
                System.out.println(lastState.get().getStateString() + " ran propperly.");
            }
            currentState.get().init();
        }
        lastState.set(currentState.get());
        lastStateSet = true;
        currentState.get().periodic();
    }

    private void setMode(CrashCheckStates newState) {
        if (currentState.get() != newState) {
            currentState.set(newState);
            switch (newState) {
                case DISABLED:
                    DriverStationSim.setEnabled(false);
                    break;
                case TELEOP:
                    DriverStationSim.setEnabled(true);
                    DriverStationSim.setAutonomous(false);
                    DriverStationSim.setTest(false);
                    break;
                case AUTONOMOUS:
                    DriverStationSim.setEnabled(true);
                    DriverStationSim.setAutonomous(true);
                    DriverStationSim.setTest(false);
                    break;
                case TEST:
                    DriverStationSim.setEnabled(true);
                    DriverStationSim.setAutonomous(false);
                    DriverStationSim.setTest(true);
                    break;
            }
        }
    }

    // L
    @Override
    public void startCompetition() {
        robot.robotInit();

        System.out.println("********** Robot program startup complete **********");

        // Having to use this is actually crazy, massive skill issue ngl
        startTime = System.nanoTime();

        while (!m_robotMainOverridden) {
            double testTime = (System.nanoTime() - startTime) / 1e9;

            DriverStationSim.notifyNewData();
            DriverStation.refreshData();

            // Run test sequence
            // System.out.println("Test Time: " + testTime);

            // State Transitions
            if (testTime < 2.0) {
                setMode(CrashCheckStates.DISABLED);
            } else if (testTime < 4.0) {
                setMode(CrashCheckStates.TELEOP);
            } else if (testTime < 6.0) {
                setMode(CrashCheckStates.AUTONOMOUS);
            } else if (testTime < 8.0) {
                setMode(CrashCheckStates.TEST);
            } else {
                System.out.println(currentState.get().getStateString() + " ran propperly.");
                System.out.println("Test completed after " + testTime + " seconds");
                break;
            }

            updateState();

            if (currentState.get() == CrashCheckStates.DISABLED) {
                DriverStationJNI.observeUserProgramDisabled();
            } else if (currentState.get() == CrashCheckStates.TELEOP) {
                DriverStationJNI.observeUserProgramTeleop();
            } else if (currentState.get() == CrashCheckStates.AUTONOMOUS) {
                DriverStationJNI.observeUserProgramAutonomous();
            } else if (currentState.get() == CrashCheckStates.TEST) {
                DriverStationJNI.observeUserProgramTest();
            }

            NetworkTableInstance.getDefault().flushLocal();

            // This the periodic thingy or sum or other
            try {
                Thread.sleep(20); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("********** Competition Ended **********");
    }

    @Override
    public void endCompetition() {
        m_robotMainOverridden = true;
    }
}