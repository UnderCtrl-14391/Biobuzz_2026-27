package org.firstinspires.ftc.teamcode.autonomos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Liberador;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;

@Autonomous (name = "Auto2Tip", group = "Autonomous")
public class Auto2Tip extends AutoFunction {
    private Intake intake;
    private Shooter shooter;
    private Liberador liberador;

    @Override
    public void runOpMode() {
        HardwareMap(hardwareMap);
        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);
        liberador = new Liberador(hardwareMap);

        waitForStart();

        if (opModeIsActive()) {

            shooter.ligarPolenAuto();
            while (opModeIsActive() && !shooter.estaProntoParaAtirarAuto()) {
            }
            sleep(100);
            liberador.liberarTrava2();
            setFlowers(true);
            intake.coletarAutonomo();
            sleep(200);
            setFlowers(false);

            stopResetMotors();
            autonomous(400, 400, 400, 400, 0.5, 0.5, 0, 0, 0, 1, 0, 0, 3200, 1200);

            stopResetMotors();
            autonomous(2500, -2500, 2500, -2500, 0.5, 0.5, 0, 0, 0, 1, 0, 0, 0, 3000);

            stopResetMotors();
            autonomous(-2000, 2000, 2000, -2000, 0.5, 0.5, 0, 0, 0, 1, 0, 0, 0, 1400);

            stopResetMotors();
            autonomous(800, 800, 800, 800, 0.5, 0.5, 0, 0, 0, 1, 0, 0, 3200, 1200);

            stopResetMotors();
            autonomous(-400, -400, -400, -400, 0.5, 0.5, 0, 0, 0, 1, 0, 0, 0, 700);

            stopResetMotors();
            autonomous(600, -600, 600, -600, 0.5, 0.5, 0, 0, 0, 1, 0, 0, 0, 900);

            stopResetMotors();
            autonomous(-5500, -5500, -5500, -5500, 0.6, 0.6, 0, 0, 0, 1, 0, 0, 0, 3000);

            stopResetMotors();
            autonomous(3000, -3000, 3000, -3000, 0.5, 0.5, 0, 0, 0, 1, 0, 0, 0, 1200);






        }

    }
}
