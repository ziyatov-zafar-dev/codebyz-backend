package uz.codebyz.user.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class SocialLinks {
    private String telegram;
    private String github;
    private String website;
    private String instagram;
    private String facebook;
    private String linkedin;
    private String twitter;

    public SocialLinks(String telegram, String github, String website, String instagram, String facebook, String linkedin, String twitter) {
        this.telegram = telegram;
        this.github = github;
        this.website = website;
        this.instagram = instagram;
        this.facebook = facebook;
        this.linkedin = linkedin;
        this.twitter = twitter;
    }

    public SocialLinks() {}

    public String getTelegram() { return telegram; }
    public void setTelegram(String telegram) { this.telegram = telegram; }

    public String getGithub() { return github; }
    public void setGithub(String github) { this.github = github; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }

    public String getFacebook() { return facebook; }
    public void setFacebook(String facebook) { this.facebook = facebook; }

    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }

    public String getTwitter() { return twitter; }
    public void setTwitter(String twitter) { this.twitter = twitter; }
}
