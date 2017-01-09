package com.machinelearning;

import com.google.gson.Gson;
import com.machinelearning.linear.regression.Model;
import com.machinelearning.nn.Program;

import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Path("/result")
public class MainService {
    Model model;

    public MainService() {
        model = new Model();
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("/train-err")
    @Produces("text/plain")
    public String getTrainErrors() {
        //
        ArrayList<Double> list = model.getTrainErrors();
        /*= new ArrayList<>();
        list.add(10.0);
        list.add(15.0);
        list.add(15.0);
        list.add(14.0);
        list.add(20.0);
        list.add(7.0);
        list.add(25.0);
        list.add(4.0);
        list.add(30.0);
        list.add(6.0);
        list.add(35.0);
        list.add(2.0);*/

        String json = new Gson().toJson(list);

        return json;
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("/validation-err")
    @Produces("text/plain")
    public String getValidationErrors() {
        ArrayList<Double> list = model.getValidationErrors();
        String json = new Gson().toJson(list);

        return json;
    }
}