package io.nuls.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nuls.core.basic.Result;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.nuls.v2.constant.Constant.CONTRACT_MINIMUM_PRICE;
import static io.nuls.v2.constant.Constant.MAX_GASLIMIT;

public class AccountBalanceTest {

    String url = "https://api.nuls.io/";

    @Before
    public void before() {
        NulsSDKBootStrap.initMain(url);
    }

    @Test
    public void accountBalance() throws JsonProcessingException {
        BigInteger result = BigInteger.ZERO;
        System.out.println(BLOCK_HOLE_ADDRESS_SET.size());
        for(String address : BLOCK_HOLE_ADDRESS_SET) {
            Map map = (Map) NulsSDKTool.getAccountBalance(address, 1, 1).getData();
            try {
                result = result.add(new BigInteger(map.get("total").toString()));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        System.out.println(result);
    }


    public static Set<String> BLOCK_HOLE_ADDRESS_SET = new HashSet<>();

    static {
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgWAwX7MbvcFSLYqMoyn88d5x3AcUww");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgaXkFL7uYEhvC8zqjkYDNY5GQwrHos");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgdZh1GWTN7a6P92zThPC77EuDPt3N2");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgjVAEJi5ZZZs7hyrPt4xMv3uGTucGj");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgfSqGPCh97oXmGDD9rQqodNZbLivzc");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgiVWU4LMyQts6YXPFYsuQz8vDZss7J");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgfHVsVx1DYv9RouqKUyKnx6qwDtQMr");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgU1DEYKJMwMnvxQmwY9C4CAMFsEGov");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgX6CRCL8PnDU2rWCDWAo45Bv3nnHJa");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HghrwiGsdkvPLk6h3AmgiEayK9XgPcD");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgexB2yTuB3UEvo1z62V2XeHeksWnNf");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgTwwcmqb1AAxXFMjHQtwx5r96bjarV");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgZn6h34EdiPZW3uftAwktqaxEP7Jr1");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgYq9bZFPPYAT2AGfskNd2xBocE9DDG");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HggLHaWAgr57BzAujWujK7cnkdfUpDv");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgY2RHpL5qUzSZY3e5dDXTS7kKTwwkE");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgZn9PCmNMYEVLktW12NojNymk6JoFD");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgcjP6h7xbVgBzTXEWVKSZrAW5JMgEe");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HghNkGyb8XHcFrsLj9mdxyHB3dwqLYh");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgXjnzZjPQUqhxdsDnGQxinDPJ1wyUU");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HghLcS3B6kAc9929wSSX6F2gxTsCfjF");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgfUkXZduCFcWieYA1t9sieEnz7jjxL");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgeLcgDeAU3fJpwwf196kQdtJ6WhAyP");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgfTFrdqGCiB1SXZY5WZBjSUtgPLVCJ");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6Hggmxe4LDEcuYkfUVxJboJFdMA9vS2m");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgZfkCMz4oVMm2Dp9qPu53zL9XpMDGo");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgiW5AGRUqshNb6TqEZraRg1QUMaQ44");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgaFggHtSTyBA9H8uCJo14FEcivvXzf");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgiUvUtuyo2AK8xaXKBR7byo5AuK34T");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgiCnpKWd22i3FKqy4EZSRcvyDGKWNb");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgbvthvopoJjwcs8Y9xQbMTTgwLFjoz");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgeLxxtebLWDhTuULMHe1NFTPDo6swg");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6Hgd9RipQqzW19cpymhbLW1Pjeq4WkJA");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgaAqUVTRWGGsdz3AhzGKAshUbRQiKX");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6Hgeujp7b2Eox8TZzBHTfQYERzaMmCot");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgWQJbdYVjA3NUsrVP9g8hbvimJsKLr");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgjYpHJXbZ5mpzvRgG923nssNPRP188");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgWCZHGDuAHgRnJSjoLhXoDmPFv5Sbm");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgaYBps2XoqtGTJBp95Q3ET6M87oD36");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgX6tbavRbQBzYdiUAWG1BW8gzSmFq9");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgVtCN6CpceEKhRexa1zCJw6sXdBtqM");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HghSFFmFHPECZXrqv1Jo1ZmUZBbwuFM");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgdeVgSdz6653mWv6VdQeoUMkEuYGgi");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HggqMzNMQ1khS6sHxmPss79pCYPPEgS");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgfqCPU5AdwCbxQK62hd4q4USP27nAY");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgVwvoGs69TfvUNcPm5W4x9UYcW1QdR");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgXjfFD5HLYLm7vcNxjCM9x3iVZSvWR");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgfY5oVvjkCLz7vyBRdPQXppCcRxkCR");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6Hgd9mLzsEjGHFQ8igyzUEiGxCzG7mwg");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6Hgh3NYZAnsEqb83iYzzHrKxpZQNpbyS");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6Hgib3NyJWaGcNeMUyrAwBRhNp2zXYzi");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgaHjr4Z7GdnGc3vNLgFvXWP55hzNNX");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgZdGvSnxFPLHrLzpVzda4gtWvHKibT");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgZSxNKeFG7AwEL8MVDGZa1KwE39fPP");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgYLpfpPuZV9umGvYjJJX7EkKsUVukS");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgcCf8NfvCBjmdvDWbanWL5cFYiB8DM");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgZwFFYvEKppidUp7irjQFKADspQXaX");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgfNf8oRpKL8gsiFsR9ZwXVebHrcDAz");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgfuE7eRNu4wJrQV6fXKupuioL9cKri");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgWZmG6MSNighAjziatrvuhM1LpCF9o");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgbjaWQZrq3CoDZEed9RwAU3zyTeUAP");
        BLOCK_HOLE_ADDRESS_SET.add("NULSd6HgawRRELcuKfvf4TeyidGLqFsHo9hgM");
    }
}