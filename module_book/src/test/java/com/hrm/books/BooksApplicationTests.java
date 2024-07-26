package com.hrm.books;

import com.hrm.books.utilities.sockets.SocketServer;
import io.jsonwebtoken.Jwts;
import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;

import java.io.IOException;

@SpringBootTest
class BooksApplicationTests {

	@Test
	void contextLoads() {
		SecretKey key = Jwts.SIG.HS512.key().build();
		String hexBinary = DatatypeConverter.printHexBinary(key.getEncoded());
		System.out.printf("Key = %s", hexBinary);
	}

	@Test
	void testSocker() throws IOException {
		SocketServer server=new SocketServer();
		System.err.println("CRUSH EM T");
		server.start(6666);
	}

}
