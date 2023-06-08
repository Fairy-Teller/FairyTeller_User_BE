package jungle.fairyTeller.security;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getName();
}
