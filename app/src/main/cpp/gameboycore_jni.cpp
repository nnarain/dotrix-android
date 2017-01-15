#include <jni.h>
#include <gameboycore/gameboycore.h>
#include <functional>

using namespace gb;

namespace
{
    struct JniListener
    {
        JniListener() : env(nullptr)
        {
        }

        JNIEnv* env;
        jobject object;
        jmethodID method;
    };

    /**
     * @brief JNI GameboyCore Wrapper
     * */
    class GameboyCoreJni : public GameboyCore
    {
    public:

        void registerScanlineCallback(const JniListener& listener)
        {
            scanline_listener_ = listener;

            this->getGPU()->setRenderCallback(std::bind(&GameboyCoreJni::scanlineCallback, this, std::placeholders::_1, std::placeholders::_2));
        }

    private:
        void scanlineCallback(const GPU::Scanline& scanline, int line)
        {
            if (scanline_listener_.env != nullptr)
            {
                // get the environment
                auto env = scanline_listener_.env;
                // get the color class and field ids
                auto color_class = env->FindClass("Lio/github/nnarain/dotrix/gameboycore/Color");
                auto rid = env->GetFieldID(color_class, "r", "I");
                auto gid = env->GetFieldID(color_class, "g", "I");
                auto bid = env->GetFieldID(color_class, "b", "I");

                // create an array of colors
                auto color_array = env->NewObjectArray(160, color_class, nullptr);

                // populate color array with pixel data
                for(int i = 0; i < 160; ++i)
                {
                    auto color = env->GetObjectArrayElement(color_array, i);
                    env->SetIntField(color, rid, scanline[i].r);
                    env->SetIntField(color, gid, scanline[i].g);
                    env->SetIntField(color, bid, scanline[i].b);
                }

                // call listener callback and passing in the array of color values
                env->CallVoidMethod(scanline_listener_.object, scanline_listener_.method, color_array, line);
            }
        }

    private:
        JniListener scanline_listener_;
    };
}

extern "C"
{
    /**
     *  Create an instance of a GameboyCore
     *  @return handle to GameboyCore pointer
     * */
    jlong Java_io_github_nnarain_dotrix_gameboycore_GameboyCore_createInstance(JNIEnv* env, jclass c)
    {
        return (jlong) new GameboyCoreJni();
    }

    /**
     * Delete the GameboyCore pointer provided by the handle
     * */
    void Java_io_github_nnarain_dotrix_gameboycore_GameboyCore_release(JNIEnv* env, jclass c, jlong handle)
    {
        delete (GameboyCoreJni*)handle;
    }

    /**
     * Load ROM from a byte buffer
     * */
    void Java_io_github_nnarain_dotrix_gameboycore_GameboyCore_loadRomFromBuffer(JNIEnv* env, jclass c, jlong handle, jbyteArray byte_buffer)
    {
        auto core = (GameboyCoreJni*) handle;

        jbyte* byte_ptr = env->GetByteArrayElements(byte_buffer, 0);
        auto length     = env->GetArrayLength(byte_buffer);

        core->loadROM((uint8_t*)byte_ptr, (uint32_t)length);
    }

    void Java_io_github_nnarain_dotrix_gameboycore_GameboyCore_update(JNIEnv* env, jclass c, jlong handle, jint steps)
    {
        auto const core = (GameboyCoreJni*) handle;
        core->update(steps);
    }

    /**
     * Register scanline callback
     * */
    void Java_io_github_nnarain_dotrix_gameboycore_GameboyCore_registerScanlineCallback(JNIEnv* env, jclass c, jlong handle, jobject object)
    {
        auto object_class = env->GetObjectClass(object);
        auto method = env->GetMethodID(object_class, "onScanline", "([io/github/nnarain/dotrix/gameboycore/Color, I)V");

        if(method == 0)
        {
            auto exception_class = env->FindClass("java/lang/IllegalArgumentException");
            env->ThrowNew(exception_class, "No method 'onScanline' found in object");
        }

        JniListener listener;
        listener.env = env;
        listener.object = object;
        listener.method = method;

        auto const core = (GameboyCoreJni*) handle;
        core->registerScanlineCallback(listener);
    }
}
