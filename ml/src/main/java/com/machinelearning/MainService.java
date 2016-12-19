package com.machinelearning;

import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Path("/result")
public class MainService {
    //MeetingsManager meetingsManager;

    public MainService() {
        //meetingsManager = new MeetingsManager();
    }

    @RolesAllowed("ADMIN")
    @GET
    @Produces("application/json;charset=UTF-8")
    public List<String> getResult() {
        ArrayList<String> records = new ArrayList<>();

        //TODO: return result of calculations


        return records;
    }
}