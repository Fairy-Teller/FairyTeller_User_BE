package jungle.fairyTeller.security;

import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;
    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "Google";
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
