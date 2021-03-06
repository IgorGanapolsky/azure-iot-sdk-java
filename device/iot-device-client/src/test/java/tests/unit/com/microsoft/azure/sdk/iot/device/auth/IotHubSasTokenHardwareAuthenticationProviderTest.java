/*
*  Copyright (c) Microsoft. All rights reserved.
*  Licensed under the MIT license. See LICENSE file in the project root for full license information.
*/

package tests.unit.com.microsoft.azure.sdk.iot.device.auth;

import com.microsoft.azure.sdk.iot.deps.util.Base64;
import com.microsoft.azure.sdk.iot.device.auth.*;
import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProvider;
import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProviderTpm;
import com.microsoft.azure.sdk.iot.provisioning.security.exceptions.SecurityClientException;
import mockit.*;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Unit tests for IotHubSasTokenHardwareAuthenticationProvider.java
 * Methods: 100%
 * Lines: 100%
 */
public class IotHubSasTokenHardwareAuthenticationProviderTest
{
    @Mocked SecurityProvider mockSecurityProvider;
    @Mocked SecurityProviderTpm mockSecurityProviderTpm;
    @Mocked IotHubSSLContext mockIotHubSSLContext;
    @Mocked SSLContext mockSSLContext;
    @Mocked IotHubSasToken mockSasToken;
    @Mocked URLEncoder mockURLEncoder;
    @Mocked Base64 mockBase64;

    private static final String encodingName = StandardCharsets.UTF_8.displayName();

    private static String expectedDeviceId = "deviceId";
    private static String expectedHostname = "hostname";
    private static String expectedSasToken = "sasToken";

    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_032: [This constructor shall save the provided security provider, hostname, and device id.]
    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_033: [This constructor shall generate and save a sas token from the security provider with the default time to live.]
    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_034: [This constructor shall retrieve and save the ssl context from the security provider.]
    @Test
    public void securityProviderConstructorSavesNeededInfo() throws IOException, InvalidKeyException, SecurityClientException
    {
        //arrange
        final String someToken = "someToken";
        final byte[] tokenBytes= someToken.getBytes();
        new NonStrictExpectations()
        {
            {
                URLEncoder.encode(anyString, encodingName);
                result = someToken;

                mockSecurityProviderTpm.signWithIdentity((byte[]) any);
                result = tokenBytes;

                Base64.encodeBase64Local((byte[]) any);
                result = tokenBytes;

                URLEncoder.encode(anyString, encodingName);
                result = someToken;

                mockSecurityProviderTpm.getSSLContext();
                result = mockSSLContext;

                Deencapsulation.newInstance(IotHubSSLContext.class, new Class[] {SSLContext.class}, mockSSLContext);
                result = mockIotHubSSLContext;
            }
        };

        //act
        IotHubSasTokenAuthenticationProvider sasTokenAuthentication = new IotHubSasTokenHardwareAuthenticationProvider(expectedHostname, expectedDeviceId, mockSecurityProviderTpm);

        //assert
        String actualHostname = Deencapsulation.getField(sasTokenAuthentication, "hostname");
        String actualDeviceId = Deencapsulation.getField(sasTokenAuthentication, "deviceId");
        SecurityProviderTpm actualSecurityProvider = Deencapsulation.getField(sasTokenAuthentication, "securityProvider");
        assertEquals(expectedHostname, actualHostname);
        assertEquals(expectedDeviceId, actualDeviceId);
        assertEquals(mockSecurityProviderTpm, actualSecurityProvider);
        new Verifications()
        {
            {
                Deencapsulation.newInstance(IotHubSSLContext.class, new Class[] {SSLContext.class}, mockSSLContext);
                times = 1;
            }
        };
    }

    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_003: [If the provided security provider is not an instance of SecurityProviderTpm, this function shall throw an IllegalArgumentException.]
    @Test (expected = IllegalArgumentException.class)
    public void securityProviderConstructorThrowsForInvalidSecurityProviderInstance() throws IOException, InvalidKeyException, SecurityClientException
    {
        //act
        new IotHubSasTokenHardwareAuthenticationProvider(expectedHostname, expectedDeviceId, mockSecurityProvider);
    }

    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_023: [If the security provider throws an exception while retrieving a sas token or ssl context from it, this function shall throw an IOException.]
    @Test (expected = IOException.class)
    public void securityProviderConstructorThrowsIfRetrievingSSLContextFromSecurityProviderThrows() throws IOException, InvalidKeyException, SecurityClientException
    {
        //arrange
        final String someToken = "someToken";
        final byte[] tokenBytes= someToken.getBytes();
        new NonStrictExpectations()
        {
            {
                URLEncoder.encode(anyString, encodingName);
                result = someToken;

                mockSecurityProviderTpm.signWithIdentity((byte[]) any);
                result = tokenBytes;

                Base64.encodeBase64Local((byte[]) any);
                result = tokenBytes;

                URLEncoder.encode(anyString, encodingName);
                result = someToken;

                mockSecurityProviderTpm.getSSLContext();
                result = new SecurityClientException("");
            }
        };

        //act
        new IotHubSasTokenHardwareAuthenticationProvider(expectedHostname, expectedDeviceId, mockSecurityProviderTpm);
    }

    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_035: [If the saved sas token has expired and there is a security provider, the saved sas token shall be refreshed with a new token from the security provider.]
    @Test
    public void getRenewedSasTokenAutoRenewsFromSecurityProvider(@Mocked final System systemMock) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, SecurityClientException, InvalidKeyException
    {
        //arrange
        final String someToken = "someToken";
        final byte[] tokenBytes= someToken.getBytes();
        new Expectations()
        {
            {
                URLEncoder.encode(anyString, encodingName);
                result = someToken;

                System.currentTimeMillis();
                result = 0;

                mockSecurityProviderTpm.signWithIdentity((byte[]) any);
                result = tokenBytes;

                Base64.encodeBase64Local((byte[]) any);
                result = tokenBytes;

                URLEncoder.encode(anyString, encodingName);
                result = someToken;
                
                Deencapsulation.invoke(mockSasToken, "isExpired");
                result = true;
            }
        };

        IotHubSasTokenAuthenticationProvider sasAuth = new IotHubSasTokenHardwareAuthenticationProvider(expectedHostname, expectedDeviceId, mockSecurityProviderTpm);

        //act
        Deencapsulation.invoke(sasAuth, "getRenewedSasToken");

        //assert
        new Verifications()
        {
            {
                mockSecurityProviderTpm.signWithIdentity((byte[]) any);
                times = 2;
            }
        };
    }

    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_005: [This function shall return the saved sas token.]
    @Test
    public void getSasTokenReturnsSavedValue() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, InvalidKeyException, SecurityClientException
    {
        //arrange
        new NonStrictExpectations()
        {
            {
                mockSasToken.toString();
                result  = expectedSasToken;

                Deencapsulation.newInstance(IotHubSasToken.class, new Class[] {String.class, String.class, String.class, String.class, long.class}, anyString, anyString, anyString, anyString, anyLong);
                result = mockSasToken;

                Deencapsulation.invoke(mockSasToken, "isExpired");
                result = false;

                URLEncoder.encode(anyString, encodingName);
                result = "some token scope";

                mockSecurityProviderTpm.signWithIdentity((byte[]) any);
                result = "some token".getBytes();
            }
        };

        IotHubSasTokenAuthenticationProvider sasAuth = new IotHubSasTokenHardwareAuthenticationProvider(expectedHostname, expectedDeviceId, mockSecurityProviderTpm);

        //act
        String actualSasToken = Deencapsulation.invoke(sasAuth, "getRenewedSasToken");

        //assert
        assertEquals(mockSasToken.toString(), actualSasToken);
    }

    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_001: [This function shall throw an UnsupportedOperationException.]
    @Test (expected = UnsupportedOperationException.class)
    public void setPathToCertificateThrows() throws IOException, InvalidKeyException, SecurityClientException
    {
        //arrange
        securityProviderExpectations();
        IotHubSasTokenAuthenticationProvider sasAuth = new IotHubSasTokenHardwareAuthenticationProvider(expectedHostname, expectedDeviceId, mockSecurityProviderTpm);

        //act
        sasAuth.setPathToIotHubTrustedCert("any string");
    }

    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_002: [This function shall throw an UnsupportedOperationException.]
    @Test (expected = UnsupportedOperationException.class)
    public void setCertificateThrows() throws IOException, InvalidKeyException, SecurityClientException
    {
        //arrange
        securityProviderExpectations();
        IotHubSasTokenAuthenticationProvider sasAuth = new IotHubSasTokenHardwareAuthenticationProvider(expectedHostname, expectedDeviceId, mockSecurityProviderTpm);

        //act
        sasAuth.setIotHubTrustedCert("any string");
    }

    private void securityProviderExpectations() throws UnsupportedEncodingException, InvalidKeyException, SecurityClientException
    {
        new NonStrictExpectations()
        {
            {
                mockSasToken.toString();
                result  = expectedSasToken;

                Deencapsulation.newInstance(IotHubSasToken.class, new Class[] {String.class, String.class, String.class, String.class, long.class}, anyString, anyString, anyString, anyString, anyLong);
                result = mockSasToken;

                Deencapsulation.invoke(mockSasToken, "isExpired");
                result = false;

                URLEncoder.encode(anyString, encodingName);
                result = "some token scope";

                mockSecurityProviderTpm.signWithIdentity((byte[]) any);
                result = "some token".getBytes();
            }
        };
    }

    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_008: [This function shall return the generated IotHubSSLContext.]
    @Test
    public void getSSLContextSuccess() throws IOException, InvalidKeyException, SecurityClientException
    {
        //arrange
        final String someToken = "someToken";
        final byte[] tokenBytes= someToken.getBytes();
        new NonStrictExpectations()
        {
            {
                URLEncoder.encode(anyString, encodingName);
                result = someToken;

                mockSecurityProviderTpm.signWithIdentity((byte[]) any);
                result = tokenBytes;

                Base64.encodeBase64Local((byte[]) any);
                result = tokenBytes;

                URLEncoder.encode(anyString, encodingName);
                result = someToken;

                mockSecurityProviderTpm.getSSLContext();
                result = mockSSLContext;

                Deencapsulation.newInstance(IotHubSSLContext.class, new Class[] {SSLContext.class}, mockSSLContext);
                result = mockIotHubSSLContext;

                mockSecurityProviderTpm.getSSLContext();
                result = mockSSLContext;

                Deencapsulation.newInstance(IotHubSSLContext.class, new Class[] {SSLContext.class}, mockSSLContext);
                result = mockIotHubSSLContext;

                Deencapsulation.invoke(mockIotHubSSLContext, "getSSLContext");
                result = mockSSLContext;
            }
        };

        IotHubSasTokenAuthenticationProvider sasTokenAuthentication = new IotHubSasTokenHardwareAuthenticationProvider(expectedHostname, expectedDeviceId, mockSecurityProviderTpm);

        //act
        SSLContext actualSSLContext = sasTokenAuthentication.getSSLContext();

        //assert
        assertEquals(mockSSLContext, actualSSLContext);
    }

    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_009: [If the token scope cannot be encoded, this function shall throw an IOException.]
    @Test (expected = IOException.class)
    public void generateSasTokenSignatureFromSecurityProviderFailsToEncodeTokenScopeThrowsIOException() throws IOException, InvalidKeyException, SecurityClientException
    {
        //arrange
        new NonStrictExpectations()
        {
            {
                URLEncoder.encode(anyString, encodingName);
                result = null;
            }
        };

        //act
        new IotHubSasTokenHardwareAuthenticationProvider(expectedHostname, expectedDeviceId, mockSecurityProviderTpm);
    }

    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_010: [If the call for the saved security provider to sign with identity returns null or empty bytes, this function shall throw an IOException.]
    @Test (expected = IOException.class)
    public void generateSasTokenSignatureFromSecurityProviderFailsToSignWithIdentityAndReturnsNullThrowsIOException() throws IOException, InvalidKeyException, SecurityClientException
    {
        //arrange
        new NonStrictExpectations()
        {
            {
                URLEncoder.encode(anyString, encodingName);
                result = "some token";

                mockSecurityProviderTpm.signWithIdentity((byte[]) any);
                result = null;
            }
        };

        //act
        new IotHubSasTokenHardwareAuthenticationProvider(expectedHostname, expectedDeviceId, mockSecurityProviderTpm);
    }

    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_010: [If the call for the saved security provider to sign with identity returns null or empty bytes, this function shall throw an IOException.]
    @Test (expected = IOException.class)
    public void generateSasTokenSignatureFromSecurityProviderFailsToSignWithIdentityAndReturnsEmptyBytesThrowsIOException() throws IOException, InvalidKeyException, SecurityClientException
    {
        //arrange
        new NonStrictExpectations()
        {
            {
                URLEncoder.encode(anyString, encodingName);
                result = "some token";

                mockSecurityProviderTpm.signWithIdentity((byte[]) any);
                result = new byte[] {};
            }
        };

        //act
        new IotHubSasTokenHardwareAuthenticationProvider(expectedHostname, expectedDeviceId, mockSecurityProviderTpm);
    }

    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_011: [When generating the sas token signature from the security provider, if an UnsupportedEncodingException or SecurityClientException is thrown, this function shall throw an IOException.]
    @Test (expected = IOException.class)
    public void generateSasTokenSignatureThrowsUnsupportedEncodingWrappedInIOException() throws IOException, InvalidKeyException, SecurityClientException
    {
        //arrange
        new NonStrictExpectations()
        {
            {
                URLEncoder.encode(anyString, encodingName);
                result = new UnsupportedEncodingException();
            }
        };

        //act
        new IotHubSasTokenHardwareAuthenticationProvider(expectedHostname, expectedDeviceId, mockSecurityProviderTpm);
    }

    //Tests_SRS_IOTHUBSASTOKENHARDWAREAUTHENTICATION_34_011: [When generating the sas token signature from the security provider, if an UnsupportedEncodingException or SecurityClientException is thrown, this function shall throw an IOException.]
    @Test (expected = IOException.class)
    public void generateSasTokenSignatureFromSecurityProviderThrowsDuringSignWithIdentityThrowsIOException() throws IOException, InvalidKeyException, SecurityClientException
    {
        //arrange
        new NonStrictExpectations()
        {
            {
                URLEncoder.encode(anyString, encodingName);
                result = "some token";

                mockSecurityProviderTpm.signWithIdentity((byte[]) any);
                result = new SecurityClientException("");
            }
        };

        //act
        new IotHubSasTokenHardwareAuthenticationProvider(expectedHostname, expectedDeviceId, mockSecurityProviderTpm);
    }
}
