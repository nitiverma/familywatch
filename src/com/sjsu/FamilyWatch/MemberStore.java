package com.sjsu.FamilyWatch;

import java.util.ArrayList;
import java.util.List;

public class MemberStore {

    private static final String TAG = "MemberStore";
    private static MemberStore sMemberStore;

    private List<Member> mMembers;

    private MemberStore() {
        mMembers = new ArrayList<Member>();
        MemberFetcher fetcher = new MemberFetcher();
        mMembers = fetcher.getMembers("123");
    }

    public static MemberStore get() {
        if (sMemberStore == null) {
            sMemberStore = new MemberStore();
        }
        return sMemberStore;
    }

    public List<Member> getMembers() {
        return mMembers;
    }

    public void setMembers(List<Member> products) {
        mMembers = products;
    }


}
