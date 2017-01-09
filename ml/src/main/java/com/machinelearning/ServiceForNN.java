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
public class ServiceForNN {
    Program program;

    public ServiceForNN() {
        program = new Program();
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("/accuracy")
    @Produces("text/plain")
    public String getAccuracy() {
        ArrayList<Double> list = Program.getAccuracy();
        String json = new Gson().toJson(list);

        return json;
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("/precision")
    @Produces("text/plain")
    public String getPrecision() {
        ArrayList<Double> list = Program.getPrecision();
        String json = new Gson().toJson(list);

        return json;
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("/recall")
    @Produces("text/plain")
    public String getRecall() {
        ArrayList<Double> list = Program.getRecall();
        String json = new Gson().toJson(list);

        return json;
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("/f1score")
    @Produces("text/plain")
    public String getF1Score() {
        ArrayList<Double> list = Program.getF1Score();
        String json = new Gson().toJson(list);

        return json;
    }
}