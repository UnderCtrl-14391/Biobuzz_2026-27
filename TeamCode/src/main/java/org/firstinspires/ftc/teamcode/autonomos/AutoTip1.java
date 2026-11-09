package org.firstinspires.ftc.teamcode.autonomos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Liberador;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;

@Autonomous (name = "AutoTip1", group = "Autonomous")
public class AutoTip1 extends AutoFunction {
    private Intake intake;
    private Shooter shooter;
    private Liberador liberador;

    @Override
    public void runOpMode() {
        HardwareMap(hardwareMap);
        waitForStart();
        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);
        liberador = new Liberador(hardwareMap);

        if (opModeIsActive()) {

            shooter.ligar();
            while (!shooter.estaProntoParaAtirar()) {
            }
            sleep(1000);
            liberador.liberarTrava2();
            setFlowers(true);
            intake.coletarAutonomo();
            sleep(4000);
            setFlowers(false);

            stopResetMotors();
            autonomous(-400, -400, -400, -400, 0.5, 0.5, 0, 0, 0, 0, 0, 0, 3200, 700);

            stopResetMotors();
            autonomous(-2000, 2000, -2000, 2000, 0.7, 0.7, 0, 0, 0, 0, 0, 0, 0, 2000);

            stopResetMotors();
            autonomous(-3200, -3200, -3200, -3200, 0.5, 0.5, 0, 0, 0, 0, 0, 0, 0, 5000);

            stopResetMotors();
            autonomous(1000,1000,-1000,1000,0.6,0.6,0,0,0,0,0,0,2000,700);

            stopResetMotors();
            autonomous(-1500, 1500, -1500, 1500, 0.7, 0.7, 0, 0, 0, 0, 0, 0, 0, 1700);

        }

    }
}
