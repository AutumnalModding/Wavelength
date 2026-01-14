package xyz.lilyflower.wavelength.redist;

public interface NoiseSampler {
	double sample(double x, double y, double yScale, double yMax);
}