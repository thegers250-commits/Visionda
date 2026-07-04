#include <iostream>
#include <cmath>
#include <thread>
#include <chrono>

// Mapeos del manifiesto
double elevation_to_freq(double h) {
    const double f0 = 60.0;
    const double k = 1.7685; // calculado para f(2.5)=5000
    return f0 * std::exp(k * h);
}

double distance_gain(double r) {
    const double r_ref = 1.0;
    return 1.0 / (1.0 + (r / r_ref) * (r / r_ref));
}

double distance_lpf_cutoff(double r) {
    const double fc0 = 12000.0;
    const double c = 0.18;
    return fc0 * std::exp(-c * r);
}

void print_example(double az, double h, double r, double luminance) {
    double freq = elevation_to_freq(h);
    double gain = distance_gain(r);
    double fc = distance_lpf_cutoff(r);
    double delta = 5.0 + 7.0 * luminance;
    double leftF = 4000.0 + delta/2.0;
    double rightF = 4000.0 - delta/2.0;

    std::cout << "Azimuth: " << az << " deg | Elevation: " << h << " m | Dist: " << r << " m\n";
    std::cout << "Mapped freq: " << freq << " Hz | Gain: " << gain << " | LPF cutoff: " << fc << " Hz\n";
    std::cout << "Light carriers -> L: " << leftF << " Hz, R: " << rightF << " Hz (delta " << delta << ")\n";
}

int main() {
    std::cout << "LibPD Host Skeleton - mapping demo\n";
    // ejemplo: simular una celda
    double az = -30.0;
    double h = 1.2;
    double r = 2.5;
    double luminance = 0.75;

    for (int i = 0; i < 5; ++i) {
        print_example(az, h, r, luminance);
        std::this_thread::sleep_for(std::chrono::milliseconds(500));
        az += 15.0; // simular movimiento
    }

    return 0;
}
