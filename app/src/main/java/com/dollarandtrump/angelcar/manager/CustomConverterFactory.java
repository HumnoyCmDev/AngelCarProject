package com.dollarandtrump.angelcar.manager;


import com.dollarandtrump.angelcar.model.NotResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class CustomConverterFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (String.class.equals(type)){
            return new Converter<ResponseBody, String>() {
                @Override
                public String convert(ResponseBody value) throws IOException {
                    return "success [not response]";
                }
            };
        }else if (NotResponse.class.equals(type)){
            return new Converter<ResponseBody, NotResponse>() {
                @Override
                public NotResponse convert(ResponseBody value) throws IOException {
                    NotResponse notResponse = new NotResponse();
                    notResponse.setNotResponse("success [not response]");
                    return notResponse;
                }
            };
        }
        return null;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if(String.class.equals(type)){
            return new Converter<String, RequestBody>() {
                @Override
                public RequestBody convert(String value) throws IOException {
                    return RequestBody.create(MediaType.parse("text/plain"),value);
                }
            };
        }
        return null;
    }

}
