package com.mahesch.trymyui.repository;

import com.mahesch.trymyui.retrofitclient.ApiService;

public class LoginP {

    private String email;
    private String password;
    private ILoginP iLoginP;
    private ApiService.ApiInterface apiInterface;

    public LoginP(String email,String password,ILoginP iLoginP)
    {
        this.email = email;
        this.password = password;
        this.iLoginP = iLoginP;
    }


    public void callLogin(){

    }



    public interface ILoginP{

        public void onSuccessL();
        public void onErrorL();
    }
}
