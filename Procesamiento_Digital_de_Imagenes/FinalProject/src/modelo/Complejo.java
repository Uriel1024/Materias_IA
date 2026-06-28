package modelo;

public class Complejo {
    public double real;
    public double imag;

    public Complejo(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public Complejo sumar(Complejo c) {
        return new Complejo(real + c.real, imag + c.imag);
    }

    public Complejo multiplicar(Complejo c) {
        return new Complejo(
            real * c.real - imag * c.imag,
            real * c.imag + imag * c.real
        );
    }

    public Complejo escalar(double s) {
        return new Complejo(real * s, imag * s);
    }

    public double magnitud() {
        return Math.sqrt(real * real + imag * imag);
    }
}
