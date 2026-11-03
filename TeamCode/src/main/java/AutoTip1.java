import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous (name = "AutoTip1", group = "Autonomous")
public class AutoTip1 extends AutoFunction {

    @Override
    public void runOpMode() {
        HardwareMap(hardwareMap);
        waitForStart();

        if (opModeIsActive()) {


            stopResetMotors();
            autonomous(0,0,0,0,0,0,0,0,0,0,6000,6000,3000);


            stopResetMotors();
            autonomous(0,0,0,0,0,0,0.8,0,0,0,6000,6000,3000);


            stopResetMotors();
            autonomous(1000,1000,1000,1000,0.7,0.7,0,0,0,0,0,0,1000);


            stopResetMotors();
            autonomous(2000,-2000,2000,-2000,0.7,0.7,0,0,0,0,0,0,2000);


            stopResetMotors();
            autonomous(5000,5000,5000,5000,0.7,0.7,0,0,0,0,0,0,4000);


            stopResetMotors();
            autonomous(2000,-2000,2000,-2000,0.7,0.7,0,0,0,0,0,0,1000);
        }
    }
}
