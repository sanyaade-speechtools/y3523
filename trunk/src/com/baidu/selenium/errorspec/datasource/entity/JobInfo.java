/**
 * 
 */
package com.baidu.selenium.errorspec.datasource.entity;

/**
 * 当前hudson的信息
 * 
 * @author sakyo
 * 
 */
public class JobInfo {

	private String getEnv(String property) {
		String result = System.getenv(property);
		if (result == null)
			return "";
		return result;
	}

	public String getBuildNumber() {
		return getEnv("BUILD_NUMBER");
	}

	public String getBuildID() {
		return getEnv("BUILD_ID");
	}

	public String getJobName() {
		return getEnv("JOB_NAME");
	}

	public String getBuildTag() {
		return getEnv("BUILD_TAG");
	}

	public String getExecutorNumber() {
		return getEnv("EXECUTOR_NUMBER");
	}

	public String getNodeName() {
		return getEnv("NODE_NAME");
	}

	public String getNodeLabels() {
		return getEnv("NODE_LABELS");
	}

	public String getJavaHome() {
		return getEnv("JAVA_HOME");
	}

	public String getWorkSpace() {
		return getEnv("WORKSPACE");
	}

	public String getHudsonUrl() {
		return getEnv("HUDSON_URL");
	}

	public String getBuildUrl() {
		return getEnv("BUILD_URL");
	}

	public String getJobUrl() {
		return getEnv("JOB_URL");
	}

	public String getSvnRevision() {
		return getEnv("SVN_REVISION");
	}

	public String getCvsBranch() {
		return getEnv("CVS_BRANCH");
	}

	public String getHudsonUser() {
		return getEnv("HUDSON_USER");
	}
	
	public String getHudsonHome(){
		return getEnv("HUDSON_HOME");
	}

	@Override
	public String toString() {
		return "JobInfo [getBuildNumber()=" + getBuildNumber() + ", getBuildID()=" + getBuildID() + ", getJobName()=" + getJobName() + ", getBuildTag()="
				+ getBuildTag() + ", getExecutorNumber()=" + getExecutorNumber() + ", getNodeName()=" + getNodeName() + ", getNodeLabels()=" + getNodeLabels()
				+ ", getJavaHome()=" + getJavaHome() + ", getWorkSpace()=" + getWorkSpace() + ", getHudsonUrl()=" + getHudsonUrl() + ", getBuildUrl()="
				+ getBuildUrl() + ", getJobUrl()=" + getJobUrl() + ", getSvnRevision()=" + getSvnRevision() + ", getCvsBranch()=" + getCvsBranch()
				+ ", getHudsonUser()=" + getHudsonUser() + "]";
	}
}
