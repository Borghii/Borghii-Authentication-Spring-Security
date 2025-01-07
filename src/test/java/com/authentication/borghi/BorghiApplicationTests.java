package com.authentication.borghi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class BorghiApplicationTests {

	Calculator calculator = new Calculator();

	@Test
	void itShouldAddTwoNumbers() {
		//given
		int numberOne = 20;
		int numberTwo = 30;

		//when
		int result = calculator.sumNumbers(numberOne,numberTwo);

		// then
		int expected = 50;

		assertThat(result).isEqualTo(expected);
	}

	class Calculator{

		public int sumNumbers(int a, int b){
			return a+b;
		}

	}

}
