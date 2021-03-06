package com.harmony.umbrella.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.harmony.umbrella.core.Member;

/**
 * @author wuxii@foxmail.com
 */
public class MemberTest {

    private static Person self;

    static {
        // 以C为起点
        self = new Person("Me");
        self.father = new Person("A");
        self.mother = new Person("B");
        self.father.spouse = self.mother;

        self.brother = new Person("D");
        self.brother.brother = self;

    }

    static class Person {

        public String name;
        public Person spouse;
        public Person father;
        public Person mother;
        public Person brother;

        public Person(String name) {
            this.name = name;
        }

    }

    @Test
    public void test() {
        Member member = MemberUtils.findMember(Person.class, "name");
        assertEquals("Me", member.get(self));
    }

}
