package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Subsistema do SHOOTER.
 *
 * Dois motores que giram juntos e são controlados por VELOCIDADE (não por potência).
 * O controlador PIDF interno do hub mantém a velocidade constante mesmo com a bateria caindo.
 *
 * Todas as velocidades públicas deste arquivo estão em RPM (rotações por minuto) do MOTOR.
 * O hub trabalha em "ticks por segundo" do encoder; a conversão é feita aqui mesmo,
 * usando TICKS_POR_VOLTA_DO_MOTOR, para NÃO depender do tipo de motor cadastrado
 * na configuração do Driver Station:
 *   ticks/s = RPM × TICKS_POR_VOLTA_DO_MOTOR ÷ 60
 *   RPM     = ticks/s × 60 ÷ TICKS_POR_VOLTA_DO_MOTOR
 */
public class Shooter {

    // ===================== CONSTANTES DE AJUSTE =====================

    /** Velocidade que o shooter tenta alcançar quando é ligado (RPM). */
    public static double VELOCIDADE_ALVO_RPM = 6200.0;

    /** Velocidade mínima para o shooter ser considerado pronto para atirar (RPM). */
    public static double VELOCIDADE_MINIMA_PARA_ATIRAR_RPM = 1172.0;

    // Coeficientes do controlador PIDF de velocidade
    public static double PIDF_PROPORCIONAL = 1.26;
    public static double PIDF_INTEGRAL = 0.126;
    public static double PIDF_DERIVATIVO = 0.0;
    public static double PIDF_FEEDFORWARD = 12.6;

    /** Coeficiente proporcional do controle de posição (configurado no motor, mas não usado no teleop). */
    public static double PROPORCIONAL_POSICAO = 5.0;

    /**
     * Quantos ticks o encoder conta em UMA volta do eixo do motor.
     * Motores sem redução (goBILDA 6000 RPM / REV HD Hex sem caixa) = 28.
     * Se o motor tiver caixa de redução, multiplique pela redução (ex.: 28 × 3.7 = 103.6).
     */
    public static double TICKS_POR_VOLTA_DO_MOTOR = 28.0;

    /** Segundos em um minuto (usado na conversão entre ticks/s e RPM). */
    private static final double SEGUNDOS_POR_MINUTO = 60.0;

    // ===================== HARDWARE =====================

    private final DcMotorEx motorShooter1;
    private final DcMotorEx motorShooter2;

    /** Última velocidade pedida aos motores, em RPM (usada na telemetria). */
    private double velocidadeAlvoRPM = 0.0;

    /**
     * Construtor: busca os motores do shooter, define sentido, freio e aplica os coeficientes PIDF.
     *
     * @param hardwareMap mapa de hardware recebido do OpMode
     */
    public Shooter(HardwareMap hardwareMap) {
        // Os textos entre aspas são os nomes cadastrados na configuração do Driver Station
        motorShooter1 = hardwareMap.get(DcMotorEx.class, "shooter");
        motorShooter2 = hardwareMap.get(DcMotorEx.class, "shooter2");

        motorShooter1.setDirection(DcMotor.Direction.FORWARD);
        motorShooter2.setDirection(DcMotor.Direction.FORWARD);

        // BRAKE: o motor trava quando a potência é zero
        motorShooter1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorShooter2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // RUN_USING_ENCODER é necessário para o controle por velocidade funcionar
        motorShooter1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorShooter2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motorShooter1.setVelocityPIDFCoefficients(PIDF_PROPORCIONAL, PIDF_INTEGRAL, PIDF_DERIVATIVO, PIDF_FEEDFORWARD);
        motorShooter2.setVelocityPIDFCoefficients(PIDF_PROPORCIONAL, PIDF_INTEGRAL, PIDF_DERIVATIVO, PIDF_FEEDFORWARD);
        motorShooter1.setPositionPIDFCoefficients(PROPORCIONAL_POSICAO);
        motorShooter2.setPositionPIDFCoefficients(PROPORCIONAL_POSICAO);
    }

    /** Liga o shooter na velocidade alvo (VELOCIDADE_ALVO_RPM). */
    public void ligar() {
        definirVelocidadeRPM(VELOCIDADE_ALVO_RPM);
    }

    /** Para o shooter (velocidade alvo = 0). */
    public void parar() {
        definirVelocidadeRPM(0.0);
    }

    /**
     * Define a velocidade alvo dos dois motores do shooter.
     *
     * @param velocidadeRPM velocidade em rotações por minuto (0 = parado)
     */
    public void definirVelocidadeRPM(double velocidadeRPM) {
        velocidadeAlvoRPM = velocidadeRPM;
        double ticksPorSegundo = converterRPMParaTicksPorSegundo(velocidadeRPM);
        motorShooter1.setVelocity(ticksPorSegundo);
        motorShooter2.setVelocity(ticksPorSegundo);
    }

    /**
     * Diz se o shooter já está rápido o suficiente para atirar.
     * Usa a média dos dois motores e compara com VELOCIDADE_MINIMA_PARA_ATIRAR_RPM.
     *
     * @return true se a velocidade média for maior ou igual à mínima para atirar
     */
    public boolean estaProntoParaAtirar() {
        return getVelocidadeMediaRPM() >= VELOCIDADE_MINIMA_PARA_ATIRAR_RPM;
    }

    /** @return a última velocidade alvo pedida, em RPM */
    public double getVelocidadeAlvoRPM() {
        return velocidadeAlvoRPM;
    }

    /** @return a velocidade real medida no motor shooter 1, em RPM */
    public double getVelocidadeShooter1RPM() {
        return converterTicksPorSegundoParaRPM(getVelocidadeShooter1TicksPorSegundo());
    }

    /** @return a velocidade real medida no motor shooter 2, em RPM */
    public double getVelocidadeShooter2RPM() {
        return converterTicksPorSegundoParaRPM(getVelocidadeShooter2TicksPorSegundo());
    }

    /** @return a velocidade crua do encoder do shooter 1, em ticks por segundo (sem conversão) */
    public double getVelocidadeShooter1TicksPorSegundo() {
        return motorShooter1.getVelocity();
    }

    /** @return a velocidade crua do encoder do shooter 2, em ticks por segundo (sem conversão) */
    public double getVelocidadeShooter2TicksPorSegundo() {
        return motorShooter2.getVelocity();
    }

    /** @return o nome do tipo de motor cadastrado na configuração do Driver Station (para conferência) */
    public String getTipoDeMotorConfigurado() {
        return motorShooter1.getMotorType().getName();
    }

    /** @return a média da velocidade real dos dois motores, em RPM */
    public double getVelocidadeMediaRPM() {
        return (getVelocidadeShooter1RPM() + getVelocidadeShooter2RPM()) / 2.0;
    }

    /**
     * Converte RPM do motor para ticks por segundo do encoder.
     *
     * @param rpm velocidade em rotações por minuto
     * @return velocidade em ticks por segundo
     */
    private static double converterRPMParaTicksPorSegundo(double rpm) {
        return rpm * TICKS_POR_VOLTA_DO_MOTOR / SEGUNDOS_POR_MINUTO;
    }

    /**
     * Converte ticks por segundo do encoder para RPM do motor.
     *
     * @param ticksPorSegundo velocidade em ticks por segundo
     * @return velocidade em rotações por minuto
     */
    private static double converterTicksPorSegundoParaRPM(double ticksPorSegundo) {
        return ticksPorSegundo * SEGUNDOS_POR_MINUTO / TICKS_POR_VOLTA_DO_MOTOR;
    }
}
