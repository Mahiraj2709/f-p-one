package providers.fairrepair.service.fairrepairpartner.data.remote;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import providers.fairrepair.service.fairrepairpartner.data.BusEvent;

public class UnauthorisedInterceptor implements Interceptor {
    Context mContent = null;
    Bus eventBus;
    public UnauthorisedInterceptor(Context mContext) {
        this.mContent = mContext;
    }
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            if (response.code() == 401) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        eventBus.post(new BusEvent.AuthenticationError());
                    }
                });
            }
            return response;
        }
}
