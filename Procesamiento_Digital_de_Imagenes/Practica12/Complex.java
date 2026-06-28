public class Complex {

    public double real;
    public double imag;

    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public Complex add(Complex c) {
        return new Complex(real + c.real, imag + c.imag);
    }

    public Complex multiply(Complex c) {
        return new Complex(
                real * c.real - imag * c.imag,
                real * c.imag + imag * c.real
        );
    }

    public Complex scale(double s) {
        return new Complex(real * s, imag * s);
    }

    public double magnitude() {
        return Math.sqrt(real * real + imag * imag);
    }
}