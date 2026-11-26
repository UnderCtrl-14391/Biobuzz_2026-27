package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Vision;

@TeleOp(name = "TeleOp Camera", group = "Test")
public class teleopcamera extends LinearOpMode {

    private Vision vision;

    @Override
    public void runOpMode() {
        vision = new Vision(hardwareMap);

        if (!vision.isConectado()) {
            telemetry.addData("Aviso", "Câmera / Webcam 1 ('camera') não encontrada na configuração");
        }

        telemetry.addData("Status", "Inicializado - Pressione START para ver AprilTags");
        telemetry.addData("Controles", "DPAD_DOWN = Parar Streaming | DPAD_UP = Retomar Streaming");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Controla streaming de vídeo com os botões direcionais do gamepad1
            vision.controlarStreaming(gamepad1.dpad_down, gamepad1.dpad_up);

            // Atualiza telemetria com as detecções das AprilTags
            vision.atualizarTelemetria(telemetry);

            if (vision.temAprilTag()) {
                telemetry.addData("Status AprilTag", "TAG DETECTADA!");
            } else {
                telemetry.addData("Status AprilTag", "Procurando tags...");
            }

            telemetry.update();
        }

        if (vision != null) {
            vision.fechar();
        }
    }
}
