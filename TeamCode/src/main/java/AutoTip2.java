import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous
public class AutoTip2 extends AutoFunction{

    public void runOpMode(){
        HardwareMap(hardwareMap);
        waitForStart();

        stopResetMotors();
        autonomous(0,0,0,0,0,0,0,0,0,0,6000,6000,3000);

        stopResetMotors();
        autonomous(0,0,0,0,0,0,0.8,0,0,0,6000,6000,3000);

        stopResetMotors();
        autonomous(1000,1000,1000,1000,0.6,0.6,0,0,0,0,0,0,2000);

        stopResetMotors();
        autonomous(-1000,1000,-1000,1000,0.6,0.6,0,0,0,0,0,0,1000);

        stopResetMotors();
        autonomous(-1000,-1000,-1000,-1000,0.2,0.2,0.8,0,0,0,0,0,1000);

        stopResetMotors();
        autonomous(100,-100,-100,100,0.3,0.3,0,0,0,0,6000,6000,2000);

        stopResetMotors();
        autonomous(0,0,0,0,0,0,0.8,0,0,0,6000,6000,3000);

        stopResetMotors();
        autonomous(-3000,3000,3000,-3000,0.6,0.6,0,0,0,0,0,0,2000);
    }
}
