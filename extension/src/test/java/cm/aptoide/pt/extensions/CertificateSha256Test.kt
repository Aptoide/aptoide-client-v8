package cm.aptoide.pt.extensions

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// AND-954: the signature an install report carries is the SHA-256 of the signing certificate.
internal class CertificateSha256Test {

  @Test
  fun `A certificate's SHA-256 is 64 lower-case hex characters without colons`() = scenario {
    m Given "the bytes of \"abc\", whose SHA-256 is the FIPS 180-2 test vector"
    val certificate = "abc".toByteArray()

    m When "its SHA-256 is written"
    val hash = certificateSha256(certificate)

    m Then "it is the known digest, in lower case and with no separators"
    assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", hash)
  }

  @Test
  fun `Bytes below 0x10 keep their leading zero`() = scenario {
    m Given "the 448-bit FIPS 180-2 message, whose SHA-256 holds the bytes 0x06 and 0x0c"
    val certificate =
      "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq".toByteArray()

    m When "its SHA-256 is written"
    val hash = certificateSha256(certificate)

    m Then "it is the known digest, every byte two characters"
    assertEquals("248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1", hash)
  }
}
