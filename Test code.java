// Mock DOM environment (for Jest testing)
const { JSDOM } = require('jsdom');
const { window } = new JSDOM('<!DOCTYPE html><div id="history"></div><div id="result"></div>');
global.document = window.document;
global.window = window;

// Import calculator core logic (adjust path as needed)
const {
    calculator,
    inputDigit,
    inputDot,
    inputOperator,
    calculate,
    backspace,
    resetCalculator,
    formatResult
} = require('./calculator'); // Assume core code is in calculator.js

// Test setup: Reset calculator state before each test
beforeEach(() => {
    resetCalculator();
});

// 1. Number input tests
describe('Number Input Logic', () => {
    test('Initial number input should replace default 0', () => {
        inputDigit('5');
        expect(calculator.currentValue).toBe('5');
    });

    test('Consecutive number inputs should concatenate into multi-digit numbers', () => {
        inputDigit('1');
        inputDigit('2');
        inputDigit('3');
        expect(calculator.currentValue).toBe('123');
    });

    test('Number input after operator should reset current value', () => {
        inputDigit('5');
        inputOperator('+');
        inputDigit('3');
        expect(calculator.currentValue).toBe('3');
    });
});

// 2. Decimal point tests
describe('Decimal Point Handling', () => {
    test('First decimal input should display 0.', () => {
        inputDot();
        expect(calculator.currentValue).toBe('0.');
    });

    test('Decimal input after number should concatenate correctly', () => {
        inputDigit('7');
        inputDot();
        expect(calculator.currentValue).toBe('7.');
    });

    test('Cannot input multiple consecutive decimals', () => {
        inputDigit('4');
        inputDot();
        inputDot();
        expect(calculator.currentValue).toBe('4.');
    });

    test('Decimal input after operator should display 0.', () => {
        inputDigit('9');
        inputOperator('-');
        inputDot();
        expect(calculator.currentValue).toBe('0.');
    });
});

// 3. Operator and calculation tests
describe('Calculation Logic', () => {
    test('Addition should return correct result', () => {
        inputDigit('5');
        inputOperator('+');
        inputDigit('3');
        calculate();
        expect(calculator.currentValue).toBe('8');
    });

    test('Subtraction should return correct result', () => {
        inputDigit('10');
        inputOperator('-');
        inputDigit('4');
        calculate();
        expect(calculator.currentValue).toBe('6');
    });

    test('Multiplication should return correct result', () => {
        inputDigit('6');
        inputOperator('×');
        inputDigit('7');
        calculate();
        expect(calculator.currentValue).toBe('42');
    });

    test('Division should return correct result', () => {
        inputDigit('20');
        inputOperator('/');
        inputDigit('5');
        calculate();
        expect(calculator.currentValue).toBe('4');
    });

    test('Modulus should return correct result', () => {
        inputDigit('10');
        inputOperator('%');
        inputDigit('3');
        calculate();
        expect(calculator.currentValue).toBe('1');
    });

    test('Chained operations should use previous result', () => {
        inputDigit('5');
        inputOperator('+');
        inputDigit('3');
        calculate(); // 5+3=8
        inputOperator('×');
        inputDigit('2');
        calculate(); // 8×2=16
        expect(calculator.currentValue).toBe('16');
    });
});

// 4. Error handling tests
describe('Error Handling Logic', () => {
    test('Division by zero should display "Error"', () => {
        inputDigit('9');
        inputOperator('/');
        inputDigit('0');
        calculate();
        expect(calculator.currentValue).toBe('Error');
    });

    test('Modulus with zero divisor should display "Error"', () => {
        inputDigit('5');
        inputOperator('%');
        inputDigit('0');
        calculate();
        expect(calculator.currentValue).toBe('Error');
    });
});

// 5. Backspace function tests
describe('Backspace Function Logic', () => {
    test('Backspace should delete the last digit', () => {
        inputDigit('1');
        inputDigit('2');
        backspace();
        expect(calculator.currentValue).toBe('1');
    });

    test('Backspace on single digit should reset to 0', () => {
        inputDigit('5');
        backspace();
        expect(calculator.currentValue).toBe('0');
    });

    test('Backspace in "Error" state should reset to 0', () => {
        inputDigit('5');
        inputOperator('/');
        inputDigit('0');
        calculate(); // Trigger "Error"
        backspace();
        expect(calculator.currentValue).toBe('0');
    });
});

// 6. Result formatting tests
describe('Result Formatting Logic', () => {
    test('Integer results should remove decimal points', () => {
        expect(formatResult(8.0)).toBe('8');
    });

    test('Decimal results should retain valid digits', () => {
        expect(formatResult(1 / 3)).toBe('0.333333');
    });

    test('Long decimals should truncate trailing zeros', () => {
        expect(formatResult(2.500000)).toBe('2.5');
    });
});

// 7. Reset function tests
describe('Reset Function Logic', () => {
    test('Reset should clear all states and display 0', () => {
        inputDigit('5');
        inputOperator('+');
        inputDigit('3');
        resetCalculator();
        expect(calculator.currentValue).toBe('0');
        expect(calculator.firstOperand).toBeNull();
        expect(calculator.operator).toBeNull();
        expect(calculator.history).toBe('');
    });
});