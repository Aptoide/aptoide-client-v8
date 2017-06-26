/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v2;

import java.util.ArrayList;

public class Comment {

  private Number id;
  private String lang;
  private String reponame;
  private String subject;
  private String text;
  private String timestamp;
  private String useridhash;
  private String username;
  private Number answerto;
  private Number votes;
  private String appname;
  private String useravatar;
  private Number appid;

  private ArrayList<Comment> subComments = new ArrayList<>();
  private boolean isShowingSubcomments;

  public Number getId() {
    return this.id;
  }

  public void setId(Number id) {
    this.id = id;
  }

  public String getLang() {
    return this.lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public String getReponame() {
    return this.reponame;
  }

  public void setReponame(String reponame) {
    this.reponame = reponame;
  }

  public String getSubject() {
    return this.subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getUseridhash() {
    return this.useridhash;
  }

  public void setUseridhash(String useridhash) {
    this.useridhash = useridhash;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Number getAnswerto() {
    return answerto;
  }

  public void setAnswerto(Number answerto) {
    this.answerto = answerto;
  }

  public ArrayList<Comment> getSubComments() {
    return subComments;
  }

  public void addSubComment(Comment subComment) {
    subComments.add(subComment);
  }

  public boolean hasSubComments() {
    return subComments.size() != 0;
  }

  public boolean isShowingSubcomments() {
    return isShowingSubcomments;
  }

  public void setShowingSubcomments(boolean isShowingSubcomments) {
    this.isShowingSubcomments = isShowingSubcomments;
  }

  public void clearSubcomments() {
    subComments.clear();
  }

  public Number getVotes() {
    return votes;
  }

  public String getAppname() {
    return appname;
  }

  public void setAppname(String appname) {
    this.appname = appname;
  }

  public String getUseravatar() {
    return useravatar;
  }

  public void setUseravatar(String useravatar) {
    this.useravatar = useravatar;
  }

  public Number getAppid() {
    return appid;
  }

  public void setAppid(Number appid) {
    this.appid = appid;
  }
}
