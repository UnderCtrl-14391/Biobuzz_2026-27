import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous
public class Auto2Tip extends AutoFunction {
    @Override
    public void runOpMode() {
        HardwareMap(hardwareMap);
        waitForStart();

        stopResetMotors();
        autonomous(0,0,0,0,0,0,0,0,0,0,6000,6000,2000);

        stopResetMotors();
        autonomous(0,0,0,0,0,0,0,0,0,0,0,0,0);
    }
}
