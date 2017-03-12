#include <jni.h>
#include <gameboycore/gameboycore.h>
#include <iostream>

using namespace gb;

namespace
{
    /**
     * @brief Wrapper for listener object
     * */
    struct JniListener
    {
        JniListener() :
                object(0),
                method(0)
        {
        }

        jobject object;
        jmethodID method;
    };

    static void throwException(JNIEnv* env, const char* msg, const char* exception_name = "java/lang/Exception")
    {
        auto exception_class = env->FindClass(exception_name);
        env->ThrowNew(exception_class, msg);
    }

    /**
     * @brief JNI GameboyCore Wrapper
     * */
    class GameboyCoreJni : public GameboyCore
    {
    public:
        GameboyCoreJni() :
                env_(nullptr)
        {
        }

        ~GameboyCoreJni()
        {
        }

        void registerScanlineCallback(const JniListener& listener)
        {
            scanline_listener_ = listener;

            this->getGPU()->setRenderCallback(std::bind(&GameboyCoreJni::scanlineCallback, this, std::placeholders::_1, std::placeholders::_2));
        }

        void setJniEnv(JNIEnv* env)
        {
            if(env == nullptr)
            {
                throw std::runtime_error("Invalid JNI Environment state");
            }

            this->env_ = env;
        }

    private:
        void scanlineCallback(const GPU::Scanline& scanline, int line)
        {
            if(env_ != nullptr && scanline_listener_.object && scanline_listener_.method)
            {
                auto pixel_array = env_->NewIntArray(scanline.size());

                if(pixel_array != 0)
                {
                    auto pixel_ptr = env_->GetIntArrayElements(pixel_array, 0);

                    for(int i = 0; i < scanline.size(); ++i)
                    {
                        auto color = 0xFF000000 | (scanline[i].r << 16) | (scanline[i].g << 8) | scanline[i].b;
                        pixel_ptr[i] = color;
                    }

                    std::cout << std::hex << scanline_listener_.object << std::endl;

                    env_->ReleaseIntArrayElements(pixel_array, pixel_ptr, 0);
                    env_->CallVoidMethod(scanline_listener_.object, scanline_listener_.method, pixel_array, line);
                }
                else
                {
                    throwException(env_, "Could not create pixel array");
                }
            }
        }

    private:
        JNIEnv* env_;
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
        auto method = env->GetMethodID(object_class, "onScanline", "([II)V");

        if(method != 0)
        {
            JniListener listener;
            listener.object = env->NewGlobalRef(object);
            listener.method = method;

            auto core = (GameboyCoreJni*) handle;
            core->registerScanlineCallback(listener);
        }
        else
        {
            throwException(env, "No method 'onScanline' found in object");
        }
    }

    void Java_io_github_nnarain_dotrix_gameboycore_GameboyCore_setJniEnv(JNIEnv* env, jclass c, jlong handle)
    {
        auto core = (GameboyCoreJni*)handle;
        core->setJniEnv(env);
    }
}
