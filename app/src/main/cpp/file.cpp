#include <jni.h>
#include <iostream>
using namespace std;

extern "C"
jstring
Java_com_wayfoo_wayfoo_MainActivity_getOauth( JNIEnv* env,
                                                  jobject thiz )
{
    unsigned char myKey[48] = { 0xAF, 0xBB, 0xB5, 0xB7, 0xB2, 0xA7, 0xB4, 0xB5, 0xB7, 0xB2, 0xA7, 0xB4, 0xB5, 0xA7, 0xA5, 0xB4, 0xA7, 0xB6, 0xB2, 0xA3, 0xB5, 0xB5, 0xB9, 0xB1, 0xB4, 0xA6, 0xB6, 0xAA, 0xA3, 0xB6, 0xBB, 0xB1, 0xB7, 0xB9, 0xAB, 0xAE, 0xAE, 0xB0, 0xA7, 0xB8, 0xA7, 0xB4, 0xA9, 0xB7, 0xA7, 0xB5, 0xB5, 0x42 };

    for (unsigned int dzxykdo = 0; dzxykdo < 48; dzxykdo++) myKey[dzxykdo] -= 0x42;
    return env->NewStringUTF((char *)myKey);
}
