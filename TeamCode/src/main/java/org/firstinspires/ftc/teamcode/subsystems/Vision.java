package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.List;

/**
 * Subsistema de Visão para AprilTag.
 *
 * Gerencia a câmera (Webcam ou Integrada), o AprilTagProcessor e o VisionPortal.
 */
public class Vision {

    private static final boolean USE_WEBCAM = true;

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private boolean conectado = false;

    /**
     * Construtor: Inicializa o AprilTagProcessor e o VisionPortal.
     *
     * @param hardwareMap mapa de hardware do OpMode
     */
    public Vision(HardwareMap hardwareMap) {
        try {
            aprilTag = AprilTagProcessor.easyCreateWithDefaults();
            if (USE_WEBCAM) {
                WebcamName webcamName = null;
                try {
                    webcamName = hardwareMap.get(WebcamName.class, "camera");
                } catch (Exception e1) {
                    try {
                        webcamName = hardwareMap.get(WebcamName.class, "Camera");
                    } catch (Exception e2) {
                        try {
                            webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
                        } catch (Exception ignored) {}
                    }
                }

                if (webcamName != null) {
                    visionPortal = VisionPortal.easyCreateWithDefaults(webcamName, aprilTag);
                    conectado = true;
                } else {
                    conectado = false;
                }
            } else {
                visionPortal = VisionPortal.easyCreateWithDefaults(BuiltinCameraDirection.BACK, aprilTag);
                conectado = true;
            }
        } catch (Exception e) {
            conectado = false;
        }
    }

    /**
     * Controla a transmissão de vídeo da câmera para economizar recursos de CPU.
     */
    public void controlarStreaming(boolean stopStreaming, boolean resumeStreaming) {
        if (visionPortal != null) {
            if (stopStreaming) {
                visionPortal.stopStreaming();
            } else if (resumeStreaming) {
                visionPortal.resumeStreaming();
            }
        }
    }

    /**
     * Retorna a lista de detecções de AprilTag ativas.
     */
    public List<AprilTagDetection> getDetections() {
        if (aprilTag != null) {
            return aprilTag.getDetections();
        }
        return null;
    }

    /**
     * Retorna true se pelo menos uma AprilTag estiver visível.
     */
    public boolean temAprilTag() {
        List<AprilTagDetection> detections = getDetections();
        return detections != null && !detections.isEmpty();
    }

    /**
     * Adiciona informações de telemetria das AprilTags detectadas.
     */
    public void atualizarTelemetria(Telemetry telemetry) {
        if (!conectado || aprilTag == null) {
            telemetry.addData("Visão (AprilTag)", "Câmera/AprilTag não encontrada");
            return;
        }

        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections != null ? currentDetections.size() : 0);

        if (currentDetections != null) {
            for (AprilTagDetection detection : currentDetections) {
                if (detection instanceof AprilTagSingleDetection) {
                    AprilTagSingleDetection singleDet = (AprilTagSingleDetection) detection;

                    if (singleDet.metadata != null) {
                        telemetry.addLine(String.format("\n==== (ID %d) %s", singleDet.id, singleDet.metadata.name));
                        telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                        telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                        telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
                    } else {
                        telemetry.addLine(String.format("\n==== (ID %d) Unknown", singleDet.id));
                        telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", singleDet.center.x, singleDet.center.y));
                    }
                } else {
                    AprilTagClusterDetection clusterDet = (AprilTagClusterDetection) detection;
                    telemetry.addLine(String.format("\n==== Tag Cluster (%s)", clusterDet.metadata.name));
                    telemetry.addLine(String.format("Percent tags found: %d", clusterDet.percentClusterFound));
                    telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                    telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                    telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
                }
            }
        }
    }

    /**
     * Fecha a câmera e encerra o VisionPortal quando o OpMode for finalizado.
     */
    public void fechar() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }

    public boolean isConectado() {
        return conectado;
    }
}
