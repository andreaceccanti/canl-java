/*
 * Copyright (c) 2011-2012 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE file for licensing information.
 *
 * Derived from the code copyrighted and licensed as follows:
 * 
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://www.eu-egee.org/partners/ for details on the copyright
 * holders.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.emi.security.authn.x509.impl;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import org.junit.Assert;
import org.junit.Test;

import eu.emi.security.authn.x509.impl.CertificateUtils.Encoding;


public class GLiteValidatorTest extends ValidatorTestBase
{
	private static final TestCase[] trustedTestCases = {
			new TestCase("trusted-certs/trusted_client", false, true),
			new TestCase("trusted-certs/trusted_client_exp", false, false),
			new TestCase("trusted-certs/trusted_clientserver", false, true),
			new TestCase("trusted-certs/trusted_clientserver_exp", false, false),
			new TestCase("trusted-certs/trusted_fclient", false, true),
			new TestCase("trusted-certs/trusted_fclient_exp", false, false),
			new TestCase("trusted-certs/trusted_none", false, true),
			new TestCase("trusted-certs/trusted_none_exp", false, false),
			new TestCase("trusted-certs/trusted_server", false, true),
			new TestCase("trusted-certs/trusted_server_exp", false, false)
	};
	
	private static final TestCase[] trustedRevokedTestCases = {
			new TestCase("trusted-certs/trusted_client_rev", false, false),
			new TestCase("trusted-certs/trusted_clientserver_rev", false, false),
			new TestCase("trusted-certs/trusted_fclient_rev", false, false),
			new TestCase("trusted-certs/trusted_none_rev", false, false),
			new TestCase("trusted-certs/trusted_server_rev", false, false)
	};
	
	private static final TestCase[] trustedProxiesTestCases = {
			new TestCase("trusted-certs/trusted_client_exp.proxy.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_client.proxy.grid_proxy", true, true),
			new TestCase("trusted-certs/trusted_client.proxy_exp.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_clientserver_exp.proxy.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_clientserver.proxy.grid_proxy", true, true),
			new TestCase("trusted-certs/trusted_clientserver.proxy_exp.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_fclient_exp.proxy.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_fclient.proxy.grid_proxy", true, true),
			new TestCase("trusted-certs/trusted_fclient.proxy_exp.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_none_exp.proxy.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_none.proxy.grid_proxy", true, true),
			new TestCase("trusted-certs/trusted_none.proxy_exp.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_server_exp.proxy.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_server.proxy.grid_proxy", true, true),
			new TestCase("trusted-certs/trusted_server.proxy_exp.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_client.proxy_rfc.grid_proxy", true, true),
			new TestCase("trusted-certs/trusted_client.proxy_rfc_plen.proxy_rfc.proxy_rfc.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_client.proxy_rfc_plen.proxy_rfc.grid_proxy", true, true),
			new TestCase("trusted-certs/trusted_client.proxy_rfc_lim.grid_proxy", true, true),
			new TestCase("trusted-certs/trusted_client.proxy_rfc.proxy.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_client.proxy_rfc_lim.proxy_rfc.grid_proxy", true, true),
			new TestCase("trusted-certs/trusted_client.proxy_rfc.proxy_rfc_lim.grid_proxy", true, true),
			new TestCase("trusted-certs/trusted_client.proxy_rfc_anyp.grid_proxy", true, true),
			new TestCase("trusted-certs/trusted_client.proxy_rfc_indep.grid_proxy", true, true)
	};
	
	private static final TestCase[] trustedRevokedProxiesTestCases = {
			new TestCase("trusted-certs/trusted_client_rev.proxy.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_clientserver_rev.proxy.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_fclient_rev.proxy.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_none_rev.proxy.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_server_rev.proxy.grid_proxy", true, false)
	};
	
	private static final TestCase[] fakeCertsTestCases = {
			new TestCase("fake-certs/fake_client", false, false),
			new TestCase("fake-certs/fake_client.proxy", false, false)
	};
	
	private static final TestCase[] fakeProxiesTestCases = {
			new TestCase("fake-certs/fake_client.proxy.grid_proxy", true, false)
	};
	
	private static final TestCase[] miscProxiesTestCases = {
			new TestCase("trusted-certs/trusted_client.proxy_dnerror2.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_client.proxy_dnerror.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_client.proxy_dnerror.proxy.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_client.proxy.proxy_dnerror.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_client.proxy_exp.proxy.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_client.proxy_exp.proxy_exp.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_client.proxy.proxy_exp.grid_proxy", true, false),
			new TestCase("trusted-certs/trusted_client.proxy.proxy.grid_proxy", true, true),
			new TestCase("trusted-certs/trusted_bigclient",false, true)
	};
	
	private static final TestCase[] subsubProxiesTestCases = {
			new TestCase("subsubca-certs/subsubca_fullchainclient.proxy.grid_proxy", true, true),
			new TestCase("subsubca-certs/subsubca_fullchainclient.proxy.proxy.grid_proxy", true, true),
			new TestCase("subsubca-certs/subsubca_client.proxy.grid_proxy", true, true),
			new TestCase("subsubca-certs/subsubca_client.proxy.proxy.grid_proxy", true, true)
	};
	
	private static final TestCase[] subsubRevokedProxiesTestCases = {
			new TestCase("subsubca-certs/subsubca_client_rev.proxy.grid_proxy", true, false),
			new TestCase("subsubca-certs/subsubca_client_rev.proxy.proxy.grid_proxy", true, false)
	};

	private static final TestCase[] subsubBadDNProxiesTestCases = {
			new TestCase("subsubca-certs/subsubca_clientbaddn.proxy.grid_proxy", true, false),
			new TestCase("subsubca-certs/subsubca_clientbaddn.proxy.proxy.grid_proxy", true, false)
	};
	
	private static final TestCase[] bigProxiesTestCases = {
			new TestCase("big-certs/big_client.proxy.grid_proxy", true, true),
			new TestCase("big-certs/big_client.proxy.proxy.grid_proxy", true, true)
	};

	protected void gliteTest(boolean expectError, TestCase tc,
			String trustStore, boolean revocation)
	{
		try
		{
			gliteTestInternal(expectError, tc, trustStore, revocation);
		} catch (Exception e)
		{
			e.printStackTrace();
			Assert.fail("Exception when processing " + tc.name
					+ ": " + e);
		}
	}
	
	protected void gliteTestInternal(boolean expectError, TestCase tc, 
			String trustStore, boolean revocation) throws Exception
	{
		System.out.println("Test Case: " + tc.name);
		
		X509Certificate[] toCheck;
		if (tc.isProxy)
		{
			KeyStore ks = CertificateUtils.loadPEMKeystore(new FileInputStream(
				"src/test/resources/glite-utiljava/" + tc.name), 
				null, "test".toCharArray());
			toCheck = CertificateUtils.convertToX509Chain(
				ks.getCertificateChain(CertificateUtils.DEFAULT_KEYSTORE_ALIAS));
		} else
		{
			toCheck = new X509Certificate[] {
					CertificateUtils.loadCertificate(new FileInputStream(
					"src/test/resources/glite-utiljava/" + tc.name + ".cert"), 
					Encoding.PEM) };
		}
		int expectedErrors = 0;
		if (expectError || !tc.valid)
			expectedErrors = Integer.MAX_VALUE;
		doPathTest(expectedErrors, "src/test/resources/glite-utiljava/grid-security/"+trustStore+"/", 
				new String[]{"*"}, ".0", 
				"src/test/resources/glite-utiljava/grid-security/"+trustStore+"/", 
				new String[]{"*"}, ".r0", 
				toCheck, null, true, revocation);
	}

	private static class TestCase
	{
		private String name;
		private boolean valid;
		private boolean isProxy;
		public TestCase(String name, boolean isProxy, boolean valid)
		{
			this.name = name;
			this.valid = valid;
			this.isProxy = isProxy;
		}
	}
	
	@Test
	public void test1()
	{
		String truststore = "certificates";
		boolean revocation = true;
		//for (TestCase tc: trustedTestCases)
		//	gliteTest(false, tc, truststore, revocation);
		//for (TestCase tc: trustedRevokedTestCases)
		//	gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: trustedProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: trustedRevokedProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: fakeCertsTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: fakeProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: miscProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: subsubProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: subsubRevokedProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: subsubBadDNProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: bigProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
	}

	@Test
	public void test2()
	{
		String truststore = "certificates-withoutCrl";
		boolean revocation = false;
		for (TestCase tc: trustedTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: trustedRevokedTestCases)
			gliteTest(true, tc, truststore, revocation);
		for (TestCase tc: trustedProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: trustedRevokedProxiesTestCases)
			gliteTest(true, tc, truststore, revocation);
		for (TestCase tc: fakeCertsTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: fakeProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: miscProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: subsubProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: subsubRevokedProxiesTestCases)
			gliteTest(true, tc, truststore, revocation);
		for (TestCase tc: subsubBadDNProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: bigProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
	}

	@Test
	public void test3()
	{
		String truststore = "certificates-withoutCrl";
		boolean revocation = true;
		for (TestCase tc: trustedTestCases)
			gliteTest(true, tc, truststore, revocation);
		for (TestCase tc: trustedRevokedTestCases)
			gliteTest(false, tc, truststore, revocation);
	}

	@Test
	public void test4()
	{
		String truststore = "certificates-rootwithpolicy";
		boolean revocation = false;
		for (TestCase tc: subsubProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: subsubRevokedProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: subsubBadDNProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
	}
	
	@Test
	public void test5()
	{
		String truststore = "certificates-subcawithpolicy";
		boolean revocation = false;
		for (TestCase tc: subsubProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: subsubRevokedProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: subsubBadDNProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
	}	

	@Test
	public void test6()
	{
		String truststore = "certificates-rootallowsubsubdeny";
		boolean revocation = false;
		for (TestCase tc: subsubProxiesTestCases)
			gliteTest(true, tc, truststore, revocation);
		for (TestCase tc: subsubRevokedProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
		for (TestCase tc: subsubBadDNProxiesTestCases)
			gliteTest(false, tc, truststore, revocation);
	}
	
	@Test
	public void testSlash()
	{
		String truststore = "certificates";
		boolean revocation = false;
		TestCase slash = new TestCase("slash-certs/slash_client_slash", false, true);
		gliteTest(false, slash, truststore, revocation);
	}
}