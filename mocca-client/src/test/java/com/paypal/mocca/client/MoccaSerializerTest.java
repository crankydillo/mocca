package com.paypal.mocca.client;

import com.paypal.mocca.client.annotation.SelectionSet;
import com.paypal.mocca.client.annotation.Var;
import com.paypal.mocca.client.sample.ComplexSampleType;
import com.paypal.mocca.client.sample.SampleRequestDTO;
import com.paypal.mocca.client.sample.SampleResponseDTO;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for {@link MoccaSerializer}
 *
 * @author fabiocarvlaho777@gmail.com
 */
public class MoccaSerializerTest {

    private final MoccaSerializer moccaSerializer = new MoccaSerializer();

    @Test
    public void simpleRequestTest() throws IOException {
        List<MoccaSerializer.Variable> variables = Arrays.asList(
                new MoccaSerializer.Variable("foo", String.class, newVar("foo")),
                new MoccaSerializer.Variable("bar", String.class, newVar("bar"))
        );

        requestTest(variables, SampleResponseDTO.class,"getOneSample", MoccaUtils.OperationType.Query,
                null, "{\n  \"query\" : \"query{getOneSample(foo: \\\"foo\\\", bar: \\\"bar\\\") {bar foo}}\"\n}");
    }

    @Test
    public void dtoRequestTest() throws IOException {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("foo", "bar");
        List<MoccaSerializer.Variable> variables = Collections.singletonList(
                new MoccaSerializer.Variable(sampleRequestDTO, SampleRequestDTO.class, newVar("sampleRequest"))
        );

        requestTest(variables, SampleResponseDTO.class,"getOneSample", MoccaUtils.OperationType.Query,
                null, "{\n  \"query\" : \"query{getOneSample(sampleRequest: {bar: \\\"bar\\\", foo: \\\"foo\\\"}) {bar foo}}\"\n}");
    }

    @Test
    public void simpleRequestNullFieldTest() throws IOException {
        List<MoccaSerializer.Variable> variables = Collections.singletonList(
                new MoccaSerializer.Variable("foo", String.class, newVar("foo"))
        );

        requestTest(variables, SampleResponseDTO.class,"getOneSample", MoccaUtils.OperationType.Mutation,
                null, "{\n  \"query\" : \"mutation{getOneSample(foo: \\\"foo\\\") {bar foo}}\"\n}");
    }

    @Test
    public void simpleRequestIgnoreTest() throws IOException {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("foo", "bar");
        Var variable = newVar("sampleRequest", "foo");
        List<MoccaSerializer.Variable> variables = Collections.singletonList(
                new MoccaSerializer.Variable(sampleRequestDTO, SampleRequestDTO.class, variable)
        );

        requestTest(variables, SampleResponseDTO.class,"getOneSample", MoccaUtils.OperationType.Query,
                null, "{\n  \"query\" : \"query{getOneSample(sampleRequest: {bar: \\\"bar\\\"}) {bar foo}}\"\n}");
    }

    @Test
    public void complexRequestTest() throws IOException {
        ComplexSampleType.ComplexField complexField = new ComplexSampleType.ComplexField(77, "sevenseven", false);
        ComplexSampleType complexSampleType = new ComplexSampleType(7, "seven", true, complexField);
        List<MoccaSerializer.Variable> variables = Collections.singletonList(
                new MoccaSerializer.Variable(complexSampleType, ComplexSampleType.class, newVar("complexSample"))
        );

        requestTest(variables, SampleResponseDTO.class, "getOneSample", MoccaUtils.OperationType.Mutation,
                null, "{\n  \"query\" : \"mutation{getOneSample(complexSample: {booleanVar: true, complexField: {innerBooleanVar: false, innerIntVar: 77, innerStringVar: \\\"sevenseven\\\"}, intVar: 7, stringVar: \\\"seven\\\"}) {bar foo}}\"\n}");
    }

    @Test
    public void complexRequestNullFieldTest() throws IOException {
        ComplexSampleType.ComplexField complexField = new ComplexSampleType.ComplexField(77, null, false);
        ComplexSampleType complexSampleType = new ComplexSampleType(7, null, true, complexField);
        List<MoccaSerializer.Variable> variables = Collections.singletonList(
                new MoccaSerializer.Variable(complexSampleType, ComplexSampleType.class, newVar("complexSample"))
        );

        requestTest(variables, SampleResponseDTO.class, "getOneSample", MoccaUtils.OperationType.Query,
                null, "{\n  \"query\" : \"query{getOneSample(complexSample: {booleanVar: true, complexField: {innerBooleanVar: false, innerIntVar: 77}, intVar: 7}) {bar foo}}\"\n}");
    }

    @Test
    public void complexRequestIgnoreTest() throws IOException {
        ComplexSampleType.ComplexField complexField = new ComplexSampleType.ComplexField(77, "sevenseven", false);
        ComplexSampleType complexSampleType = new ComplexSampleType(7, "seven", true, complexField);
        Var variable = newVar("complexSample", "intVar", "complexField.innerStringVar", "complexField.innerBooleanVar");
        List<MoccaSerializer.Variable> variables = Collections.singletonList(
                new MoccaSerializer.Variable(complexSampleType, ComplexSampleType.class, variable)
        );

        requestTest(variables, SampleResponseDTO.class, "getOneSample", MoccaUtils.OperationType.Mutation,
                null, "{\n  \"query\" : \"mutation{getOneSample(complexSample: {booleanVar: true, complexField: {innerIntVar: 77}, stringVar: \\\"seven\\\"}) {bar foo}}\"\n}");
    }

    @Test
    public void complexResponseTest() throws IOException {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("foo", "bar");
        List<MoccaSerializer.Variable> variables = Collections.singletonList(
                new MoccaSerializer.Variable(sampleRequestDTO, SampleRequestDTO.class, newVar("sampleRequest"))
        );

        requestTest(variables, ComplexSampleType.class,"getABeer", MoccaUtils.OperationType.Query,
                null, "{\n  \"query\" : \"query{getABeer(sampleRequest: {bar: \\\"bar\\\", foo: \\\"foo\\\"}) {booleanVar complexField {innerBooleanVar innerIntVar innerStringVar} intVar stringVar}}\"\n}");
    }

    @Test
    public void complexResponseCustomSelectionSetAllFieldsTest() throws IOException {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("foo", "bar");
        SelectionSet selectionSet = newSelectionSet("{booleanVar complexField {innerBooleanVar innerIntVar innerStringVar} intVar stringVar}");
        List<MoccaSerializer.Variable> variables = Collections.singletonList(
                new MoccaSerializer.Variable(sampleRequestDTO, SampleRequestDTO.class, newVar("sampleRequest"))
        );

        requestTest(variables, ComplexSampleType.class,"getABeer", MoccaUtils.OperationType.Query,
                selectionSet, "{\n  \"query\" : \"query{getABeer(sampleRequest: {bar: \\\"bar\\\", foo: \\\"foo\\\"}) {booleanVar complexField {innerBooleanVar innerIntVar innerStringVar} intVar stringVar}}\"\n}");
    }

    @Test
    public void complexResponseCustomSelectionSetLessFieldsTest() throws IOException {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("foo", "bar");
        SelectionSet selectionSet = newSelectionSet("{booleanVar complexField stringVar}");
        List<MoccaSerializer.Variable> variables = Collections.singletonList(
                new MoccaSerializer.Variable(sampleRequestDTO, SampleRequestDTO.class, newVar("sampleRequest"))
        );

        requestTest(variables, ComplexSampleType.class,"getABeer", MoccaUtils.OperationType.Query,
                selectionSet, "{\n  \"query\" : \"query{getABeer(sampleRequest: {bar: \\\"bar\\\", foo: \\\"foo\\\"}) {booleanVar complexField stringVar}}\"\n}");
    }

    private void requestTest(List<MoccaSerializer.Variable> variables, Type responseType, String operationName, MoccaUtils.OperationType operationType, SelectionSet selectionSet, String expectedRequest) throws IOException {
        byte[] requestBytes = moccaSerializer.serialize(variables, responseType, operationName, operationType, selectionSet);
        String actualRequest = new String(requestBytes);
        assertEquals(actualRequest, expectedRequest);
    }

    private Var newVar(String value, String... ignore) {
        return new Var(){
            @Override public Class<? extends Annotation> annotationType() { return Var.class; }
            @Override public String value() {return value;}
            @Override public String[] ignore() { return ignore;}
            @Override public boolean raw() { return false; }
        };
    }

    private SelectionSet newSelectionSet(String value) {
        return new SelectionSet(){
            @Override public Class<? extends Annotation> annotationType() { return SelectionSet.class; }
            @Override public String value() { return value;}
        };
    }
}