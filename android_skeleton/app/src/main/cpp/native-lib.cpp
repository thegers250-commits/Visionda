// Native JSON receiver and simple mapping functions
#include <jni.h>
#include <string>
#include <android/log.h>
#include <cmath>

#define LOG_TAG "VisualondaNative"
#define ALOG(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static double elevation_to_freq(double h) {
    const double f0 = 60.0;
    const double k = 1.7685; // calculado para f(2.5)=5000
    return f0 * std::exp(k * h);
}

static double distance_gain(double r) {
    const double r_ref = 1.0;
    return 1.0 / (1.0 + (r / r_ref) * (r / r_ref));
}

static double distance_lpf_cutoff(double r) {
    const double fc0 = 12000.0;
    const double c = 0.18;
    return fc0 * std::exp(-c * r);
}

// Minimal JSON extractors for known keys (robust enough for our sample)
static bool extract_number(const std::string &s, const std::string &key, double &out) {
    size_t pos = s.find(key);
    if (pos == std::string::npos) return false;
    pos = s.find_first_of("-0123456789", pos + key.length());
    if (pos == std::string::npos) return false;
    size_t end = pos;
    while (end < s.size() && ( (s[end] >= '0' && s[end] <= '9') || s[end]=='.' || s[end]=='-' || s[end]=='e' || s[end]=='E' || s[end]=='+' )) end++;
    try {
        out = std::stod(s.substr(pos, end-pos));
        return true;
    } catch (...) {
        return false;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_sendControlJson(JNIEnv* env, jobject /* this */, jstring jjson) {
    const char* cstr = env->GetStringUTFChars(jjson, nullptr);
    std::string s(cstr ? cstr : "");
    env->ReleaseStringUTFChars(jjson, cstr);

    ALOG("[native] Received JSON: %s", s.c_str());

    // extract first cell parameters
    double az=0.0, elev=0.0, dist=0.0, lum=0.0;
    bool hasAz = extract_number(s, "azimuth_deg", az);
    bool hasElev = extract_number(s, "elevation_m", elev);
    bool hasDist = extract_number(s, "distance_m", dist);
    bool hasLum = extract_number(s, "luminance", lum);

    if (!hasAz && !hasElev && !hasDist && !hasLum) {
        ALOG("[native] No numeric fields found in JSON.");
        return;
    }

    double freq = elevation_to_freq(elev);
    double gain = distance_gain(dist);
    double lpf = distance_lpf_cutoff(dist);
    double delta = 5.0 + 7.0 * lum;
    double leftF = 4000.0 + delta/2.0;
    double rightF = 4000.0 - delta/2.0;

    ALOG("[native] Parsed cell -> az: %.2f deg, elev: %.2fm, dist: %.2fm, lum: %.2f", az, elev, dist, lum);
    ALOG("[native] Mapping -> freq: %.2f Hz | gain: %.3f | LPF cutoff: %.1f Hz", freq, gain, lpf);
    ALOG("[native] Light carriers -> L: %.2f Hz | R: %.2f Hz (delta %.2f)", leftF, rightF, delta);

    // Send mapped parameters to DSP engine (placeholder)
    // Replace this stub with real LibPD/PD or C++ DSP calls when integrating the audio engine.
    // Example API points to implement: pd_send_float("freq", freq); pd_send_float("gain", gain); etc.
    ALOG("[native] Sending params to DSP (stub)...");
    // stub_send_to_dsp(freq, gain, lpf, leftF, rightF);
}

// Placeholder function: when LibPD or DSP engine is added, implement parameter sending here.
static void stub_send_to_dsp(double freq, double gain, double lpf, double leftF, double rightF) {
    // For now, just log -- replace with libpd bindings or FMOD/other engine calls.
    ALOG("[DSP stub] freq=%.2f gain=%.3f lpf=%.1f left=%.2f right=%.2f", freq, gain, lpf, leftF, rightF);
}

// JNI stubs for LibPD integration
extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdInit(JNIEnv* env, jobject /* this */) {
    ALOG("[native] pdInit() called - stub (implement libpd init here)");
}

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdOpenPatch(JNIEnv* env, jobject /* this */, jstring jpath) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    ALOG("[native] pdOpenPatch(%s) - stub (implement libpd openpatch)", path);
    env->ReleaseStringUTFChars(jpath, path);
}

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdSendFloat(JNIEnv* env, jobject /* this */, jstring jname, jfloat value) {
    const char* name = env->GetStringUTFChars(jname, nullptr);
    ALOG("[native] pdSendFloat(%s, %.3f) - stub (implement libpd send)", name, value);
    env->ReleaseStringUTFChars(jname, name);
}
}
