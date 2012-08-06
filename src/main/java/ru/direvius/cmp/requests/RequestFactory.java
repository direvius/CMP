/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp.requests;

import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author direvius
 */
public class RequestFactory {
    public enum SampleType {
        closeCheck, checkModifyFuel, checkModifyStiu
    }
    
    private final static Map<SampleType, Request> requests = new EnumMap<SampleType, Request>(SampleType.class);
    
    static{
        requests.put(SampleType.closeCheck, new ByteArrayRequest("01-01-04-02-01-00-05-07-20-12-05-31-08-17-04-03-04-00-02-0D-00-0A-04-00-00-00-00-0B-04-00-00-00-00-0D-04-00-00-00-00-10-04-00-00-00-00-08-7A-01-2B-01-02-47-32-02-04-00-00-2E-E0-03-04-00-00-2B-C0-04-04-00-02-0D-00-05-04-80-2D-39-32-06-01-02-07-04-00-00-00-00-08-04-00-00-1A-40-01-33-01-07-5A-5A-5A-5A-5A-5A-5A-02-04-00-0A-41-00-03-04-00-00-00-0A-04-04-00-00-1A-40-05-07-5A-61-64-61-74-6F-6B-06-01-08-07-04-00-00-00-00-08-04-00-00-00-00-02-01-02-03-04-00-02-27-40-08-07-20-12-05-31-08-17-04-09-04-00-00-1A-40"));
        requests.put(SampleType.checkModifyFuel, new ByteArrayRequest("01-01-06-03-04-00-02-0D-00-04-02-00-04-08-44-01-1C-01-02-47-32-02-04-00-00-2E-E0-03-04-00-00-2B-C0-04-04-00-02-0D-00-05-04-80-2D-39-32-03-04-00-02-0D-00-0A-1E-01-04-00-02-0D-00-02-04-00-00-00-00-03-04-00-00-00-00-04-04-00-00-00-00-05-04-00-00-00-00"));
        requests.put(SampleType.checkModifyStiu, new ByteArrayRequest("01-01-06-03-04-00-03-D0-90-04-02-00-04-08-81-96-01-47-01-07-58-37-31-34-36-35-32-02-04-00-00-03-E8-03-04-00-00-C3-50-04-04-00-00-C3-50-05-2A-8E-E7-A5-AD-EC-20-A4-AB-A8-AD-AD-AE-A5-20-AD-A0-A7-A2-A0-AD-A8-A5-20-E2-AE-A2-A0-E0-A0-20-A4-AB-EF-20-AF-E0-AE-A2-A5-E0-AA-A8-01-25-01-07-58-37-31-34-36-35-31-02-04-00-00-03-E8-03-04-00-03-0D-40-04-04-00-03-0D-40-05-08-AC-AE-A9-AA-A0-8D-85-92-03-04-00-03-D0-90-0A-1E-01-04-00-03-D0-90-02-04-00-00-00-00-03-04-00-00-00-00-04-04-00-00-00-00-05-04-00-00-00-00"));
    }
    
    public static Request get(SampleType type) {
        if(requests.containsKey(type)){
            return requests.get(type);
        } else {
                throw new IllegalStateException("Wrong sample type");
        }
    }
}
