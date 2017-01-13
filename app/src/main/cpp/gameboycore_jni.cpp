#include <jni.h>
#include <gameboycore/gameboycore.h>

using namespace gb;

extern "C"
{
    /**
     *  Create an instance of a GameboyCore
     *  @return handle to GameboyCore pointer
     * */
    jlong Java_io_github_nnarain_dotrix_gameboycore_GameboyCore_createInstance(JNIEnv* env, jclass c)
    {
        return (jlong) new GameboyCore();
    }

    /**
     * Delete the GameboyCore pointer provided by the handle
     * */
    void Java_io_github_nnarain_dotrix_gameboycore_GameboyCore_release(JNIEnv* env, jclass c, jlong handle)
    {
        delete (GameboyCore*)handle;
    }

    void Java_io_github_nnarain_dotrix_gameboycore_GameboyCore_loadRomFromBuffer(JNIEnv* env, jclass c, jlong handle, jbyteArray byte_buffer)
    {
        auto core = (GameboyCore*) handle;

        jbyte* byte_ptr = env->GetByteArrayElements(byte_buffer, 0);
        auto length     = env->GetArrayLength(byte_buffer);

        core->loadROM((uint8_t*)byte_ptr, (uint32_t)length);
    }
}
