package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Subsistema do CHASSI.
 *
 * Chassi mecanum de 4 motores, controlado de forma "field-centric" (relativa à quadra):
 * empurrar o analógico para frente sempre leva o robô para longe do piloto,
 * independente de para onde o robô está virado. Para isso usamos a IMU do Control Hub.
 */
public class Chassi {

    // ===================== CONSTANTES DE AJUSTE =====================

    /** Multiplicador aplicado a todos os movimentos quando o modo lento está ativo (0.3 = 30% da velocidade). */
    public static double MULTIPLICADOR_MODO_LENTO = 0.3;

    // ===================== HARDWARE =====================

    private final DcMotor motorFrenteEsquerda;
    private final DcMotor motorTrasEsquerda;
    private final DcMotor motorFrenteDireita;
    private final DcMotor motorTrasDireita;

    /** Sensor de orientação (giroscópio) usado para saber o ângulo do robô no campo. */
    private final IMU imu;

    /**
     * Construtor: busca os motores e a IMU na configuração do robô e prepara tudo para uso.
     *
     * @param hardwareMap mapa de hardware recebido do OpMode
     */
    public Chassi(HardwareMap hardwareMap) {
        // Os textos entre aspas são os nomes cadastrados na configuração do Driver Station
        // ("T" no final = Trás)
        motorFrenteEsquerda = hardwareMap.get(DcMotor.class, "esquerda");
        motorTrasEsquerda = hardwareMap.get(DcMotor.class, "esquerdaTras");
        motorFrenteDireita = hardwareMap.get(DcMotor.class, "direita");
        motorTrasDireita = hardwareMap.get(DcMotor.class, "direitaTras");

        // Sentido de rotação de cada motor (depende de como foram montados no robô)
        motorFrenteEsquerda.setDirection(DcMotor.Direction.FORWARD);
        motorTrasEsquerda.setDirection(DcMotor.Direction.FORWARD);
        motorFrenteDireita.setDirection(DcMotor.Direction.REVERSE);
        motorTrasDireita.setDirection(DcMotor.Direction.REVERSE);

        // Inicializa a IMU informando como o Control Hub está montado no robô
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot orientacaoDoHub = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT, // logo do hub virado para a esquerda
                RevHubOrientationOnRobot.UsbFacingDirection.UP);    // portas USB viradas para cima
        imu.initialize(new IMU.Parameters(orientacaoDoHub));
    }

    /**
     * Move o robô relativo à quadra, com opção de modo lento.
     *
     * @param frente    movimento para frente/trás (-1 a 1)
     * @param lateral   movimento para os lados (-1 a 1)
     * @param rotacao   giro no próprio eixo (-1 a 1)
     * @param modoLento se true, multiplica todos os movimentos por MULTIPLICADOR_MODO_LENTO
     */
    public void moverRelativoAQuadra(double frente, double lateral, double rotacao, boolean modoLento) {
        if (modoLento) {
            frente *= MULTIPLICADOR_MODO_LENTO;
            lateral *= MULTIPLICADOR_MODO_LENTO;
            rotacao *= MULTIPLICADOR_MODO_LENTO;
        }
        moverRelativoAQuadra(frente, lateral, rotacao);
    }

    /**
     * Move o robô relativo à quadra (field-centric).
     * Gira o vetor de movimento pelo ângulo atual do robô para que "frente"
     * signifique sempre a mesma direção da quadra.
     *
     * @param frente  movimento para frente/trás (-1 a 1)
     * @param lateral movimento para os lados (-1 a 1)
     * @param rotacao giro no próprio eixo (-1 a 1)
     */
    public void moverRelativoAQuadra(double frente, double lateral, double rotacao) {
        double anguloDoRobo = getAnguloEmRadianos();

        // Rotação do vetor (frente, lateral) pelo ângulo do robô
        double lateralCorrigida = lateral * Math.cos(anguloDoRobo) - frente * Math.sin(anguloDoRobo);
        double frenteCorrigida = lateral * Math.sin(anguloDoRobo) + frente * Math.cos(anguloDoRobo);

        moverRelativoAoRobo(frenteCorrigida, lateralCorrigida, rotacao);
    }

    /**
     * Move o robô relativo a ele mesmo (robot-centric), com opção de modo lento.
     *
     * @param frente    movimento para frente/trás (-1 a 1)
     * @param lateral   movimento para os lados (-1 a 1)
     * @param rotacao   giro no próprio eixo (-1 a 1)
     * @param modoLento se true, multiplica todos os movimentos por MULTIPLICADOR_MODO_LENTO
     */
    public void moverRelativoAoRobo(double frente, double lateral, double rotacao, boolean modoLento) {
        if (modoLento) {
            frente *= MULTIPLICADOR_MODO_LENTO;
            lateral *= MULTIPLICADOR_MODO_LENTO;
            rotacao *= MULTIPLICADOR_MODO_LENTO;
        }
        moverRelativoAoRobo(frente, lateral, rotacao);
    }

    /**
     * Move o robô relativo a ele mesmo (frente = para onde o robô está apontando).
     * Calcula a potência de cada roda mecanum e normaliza para nenhuma passar de 1.0.
     *
     * @param frente  movimento para frente/trás (-1 a 1)
     * @param lateral movimento para os lados (-1 a 1)
     * @param rotacao giro no próprio eixo (-1 a 1)
     */
    public void moverRelativoAoRobo(double frente, double lateral, double rotacao) {
        // Fórmula do mecanum convencional
        double potenciaFrenteEsquerda = frente + lateral + rotacao;
        double potenciaTrasEsquerda = frente - lateral + rotacao;
        double potenciaFrenteDireita = frente - lateral - rotacao;
        double potenciaTrasDireita = frente + lateral - rotacao;

        // Se alguma potência passar de 1.0, divide todas pelo maior valor (mantém a proporção)
        double maiorPotencia = Math.max(1.0, Math.abs(potenciaFrenteEsquerda));
        maiorPotencia = Math.max(maiorPotencia, Math.abs(potenciaTrasEsquerda));
        maiorPotencia = Math.max(maiorPotencia, Math.abs(potenciaFrenteDireita));
        maiorPotencia = Math.max(maiorPotencia, Math.abs(potenciaTrasDireita));

        motorFrenteEsquerda.setPower(potenciaFrenteEsquerda / maiorPotencia);
        motorTrasEsquerda.setPower(potenciaTrasEsquerda / maiorPotencia);
        motorFrenteDireita.setPower(potenciaFrenteDireita / maiorPotencia);
        motorTrasDireita.setPower(potenciaTrasDireita / maiorPotencia);
    }

    /** Para todos os motores do chassi. */
    public void parar() {
        moverRelativoAoRobo(0, 0, 0);
    }

    /** Zera o ângulo da IMU: a direção atual do robô passa a ser considerada "frente". */
    public void zerarAngulo() {
        imu.resetYaw();
    }

    /** @return ângulo atual do robô (yaw) em radianos */
    public double getAnguloEmRadianos() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    /** @return ângulo atual do robô (yaw) em graus, útil para telemetria */
    public double getAnguloEmGraus() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }
}
