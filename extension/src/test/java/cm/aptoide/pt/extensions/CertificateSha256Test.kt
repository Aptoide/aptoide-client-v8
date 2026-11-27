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
    m Given "the bytes of \"abc\", whose SHA-256 holds the bytes 0x01, 0x03 and 0x00"
    val certificate = "abc".toByteArray()

    m When "its SHA-256 is written"
    val hash = certificateSha256(certificate)

    m Then "every byte is two characters: 64 in all"
    assertEquals(64, hash.length)
    assertEquals("8f01", hash.substring(8, 12))
  }
}
