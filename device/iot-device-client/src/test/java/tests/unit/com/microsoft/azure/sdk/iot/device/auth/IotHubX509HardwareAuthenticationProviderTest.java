/*
*  Copyright (c) Microsoft. All rights reserved.
*  Licensed under the MIT license. See LICENSE file in the project root for full license information.
*/

package tests.unit.com.microsoft.azure.sdk.iot.device.auth;

import com.microsoft.azure.sdk.iot.device.auth.IotHubSSLContext;
import com.microsoft.azure.sdk.iot.device.auth.IotHubX509AuthenticationProvider;
import com.microsoft.azure.sdk.iot.device.auth.IotHubX509HardwareAuthenticationProvider;
import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProvider;
import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProviderX509;
import com.microsoft.azure.sdk.iot.provisioning.security.exceptions.SecurityClientException;
import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for IotHubX509HardwareAuthenticationProvider.java
 * Methods: 100%
 * Lines: 100%
 */
public class IotHubX509HardwareAuthenticationProviderTest
{
    @Mocked SecurityProviderX509 mockSecurityProviderX509;
    @Mocked SecurityProvider mockSecurityProvider;
    @Mocked IotHubSSLContext mockIotHubSSLContext;
    @Mocked SSLContext mockSSLContext;

    //Tests_SRS_IOTHUBX509HARDWAREAUTHENTICATION_34_001: [This function shall save the provided security provider.]
    @Test
    public void constructorSuccess()
    {
        //act
        IotHubX509AuthenticationProvider authentication = new IotHubX509HardwareAuthenticationProvider(mockSecurityProviderX509);

        //assert
        SecurityProvider actualSecurityProvider = Deencapsulation.getField(authentication, "securityProviderX509");
        assertEquals(mockSecurityProviderX509, actualSecurityProvider);
    }

    //Tests_SRS_IOTHUBX509HARDWAREAUTHENTICATION_34_002: [If the provided security provider is not an instance of SecurityProviderX509, an IllegalArgumentException shall be thrown.]
    @Test (expected = IllegalArgumentException.class)
    public void constructorThrowsForInvalidSecurityProviderInstance()
    {
        //act
        IotHubX509AuthenticationProvider authentication = new IotHubX509HardwareAuthenticationProvider(mockSecurityProvider);
    }

    //Tests_SRS_IOTHUBX509AUTHENTICATION_34_032: [If this object was created using a constructor that takes an SSLContext, this function shall throw an UnsupportedOperationException.]
    @Test(expected = UnsupportedOperationException.class)
    public void cannotSetNewDefaultCertPathIfConstructedWithSSLContext()
    {
        //arrange
        IotHubX509AuthenticationProvider authentication = new IotHubX509HardwareAuthenticationProvider(mockSecurityProviderX509);

        //act
        authentication.setPathToIotHubTrustedCert("any path");
    }

    //Tests_SRS_IOTHUBX509AUTHENTICATION_34_033: [If this object was created using a constructor that takes an SSLContext, this function shall throw an UnsupportedOperationException.]
    @Test (expected = UnsupportedOperationException.class)
    public void cannotSetNewDefaultCertIfConstructedWithSSLContext()
    {
        //arrange
        IotHubX509AuthenticationProvider authentication = new IotHubX509HardwareAuthenticationProvider(mockSecurityProviderX509);

        //act
        authentication.setIotHubTrustedCert("any string");
    }

    //Tests_SRS_IOTHUBX509HARDWAREAUTHENTICATION_34_004: [If the security provider throws a SecurityProviderException while generating an SSLContext, this function shall throw an IOException.]
    @Test (expected = IOException.class)
    public void getSSLContextThrowsIOExceptionIfExceptionEncountered() throws SecurityClientException, IOException
    {
        //arrange
        IotHubX509AuthenticationProvider authentication = new IotHubX509HardwareAuthenticationProvider(mockSecurityProviderX509);

        new NonStrictExpectations()
        {
            {
                mockSecurityProviderX509.getSSLContext();
                result = new SecurityClientException("");
            }
        };

        //act
        authentication.getSSLContext();
    }

    //Tests_SRS_IOTHUBX509HARDWAREAUTHENTICATION_34_003: [If this object's ssl context has not been generated yet, this function shall generate it from the saved security provider.]
    //Tests_SRS_IOTHUBX509HARDWAREAUTHENTICATION_34_005: [This function shall return the saved IotHubSSLContext.]
    @Test
    public void getSSLContextSuccess() throws SecurityClientException, IOException
    {
        //arrange
        IotHubX509AuthenticationProvider authentication = new IotHubX509HardwareAuthenticationProvider(mockSecurityProviderX509);

        new NonStrictExpectations()
        {
            {
                Deencapsulation.invoke(mockIotHubSSLContext, "getSSLContext");
                result = mockSSLContext;

                mockSecurityProviderX509.getSSLContext();
                result = mockSSLContext;

                Deencapsulation.newInstance(IotHubSSLContext.class, new Class[] { SSLContext.class }, mockSSLContext);
                result = mockIotHubSSLContext;

                Deencapsulation.invoke(mockIotHubSSLContext, "getSSLContext");
                result = mockSSLContext;
            }
        };

        Deencapsulation.setField(authentication, "iotHubSSLContext", null);

        //act
        SSLContext actualSSLContext = authentication.getSSLContext();
        assertEquals(mockSSLContext, actualSSLContext);
    }

    //Tests_SRS_IOTHUBX509HARDWAREAUTHENTICATION_34_006: [This function shall throw an UnsupportedOperationException.]
    @Test (expected = UnsupportedOperationException.class)
    public void setPathToCertificateThrows()
    {
        //arrange
        IotHubX509AuthenticationProvider auth = new IotHubX509HardwareAuthenticationProvider(mockSecurityProviderX509);

        //act
        auth.setPathToIotHubTrustedCert("any string");
    }

    //Tests_SRS_IOTHUBX509HARDWAREAUTHENTICATION_34_007: [This function shall throw an UnsupportedOperationException.]
    @Test (expected = UnsupportedOperationException.class)
    public void setCertificateThrows()
    {
        //arrange
        IotHubX509AuthenticationProvider auth = new IotHubX509HardwareAuthenticationProvider(mockSecurityProviderX509);

        //act
        auth.setIotHubTrustedCert("any string");
    }
}
