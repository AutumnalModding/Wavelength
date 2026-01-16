package xyz.lilyflower.wavelength.include;

public interface NoiseSampler {
	double sample(double x, double y, double yScale, double yMax);
}