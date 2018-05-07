package com.smartepsilon.backend;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.assertj.core.api.Assertions;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.smartepsilon.backend.exception.ExternalServiceUnhealthy;
import com.smartepsilon.gitrepo.model.GithubRepository;

public class RespositoryClientWithFallbackTest {
    
    private static final int RETRIES_COUNT = 3;
    private static final long MILIS_THRESHOLD = 2000;
    private static final String STUBBED_ENTITY_NAME = "fe34543tre3";
    private RepositoryClientWithFallback testee;
    
    private WebTarget mockedFallbackTarget = Mockito.mock(WebTarget.class);
    private WebTarget mockedWebTarget = Mockito.mock(WebTarget.class);
    
    private final ProcessingException processingException = new ProcessingException("stubbed processing exception");
     
    @BeforeMethod
    public void setup() {
        this.testee = new RepositoryClientWithFallbackImpl(mockedWebTarget, mockedFallbackTarget, MILIS_THRESHOLD, RETRIES_COUNT);
    }
    
    @Test
    public void shouldDelegateToFallbackWhenPrimaryTargetConstantlyFails() {
        //given
        Builder builder = Mockito.mock(Builder.class);
        Builder fallbackBuilder = Mockito.mock(Builder.class);
       
        Mockito.when(builder.get()).thenThrow(processingException);
        GithubRepository entity = GithubRepository.builder().withName(STUBBED_ENTITY_NAME).build();
        Mockito.when(fallbackBuilder.get()).thenReturn(Response.ok().entity(entity).build());
        
        stubTargets(builder, fallbackBuilder);       
        // when
        Response response = testee.getRepository("owner", "1");
        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getEntity()).isInstanceOf(GithubRepository.class);
        entity = GithubRepository.class.cast(response.getEntity());
        Assertions.assertThat(entity.getName()).isEqualTo(STUBBED_ENTITY_NAME);
        
        Mockito.verify(builder, Mockito.times(RETRIES_COUNT)).get();
        Mockito.verify(fallbackBuilder, Mockito.times(1)).get();
    }

    @Test
    public void shouldPerformSingleRequestCallWhenOriginalTargetHealty() {
        //given
        Builder builder = Mockito.mock(Builder.class);
        Builder fallbackBuilder = Mockito.mock(Builder.class);
       
        GithubRepository entity = GithubRepository.builder().withName(STUBBED_ENTITY_NAME).build();
        Response build = Response.ok().entity(entity).build();
        
        Mockito.when(builder.get()).thenReturn(build);
        Mockito.when(fallbackBuilder.get()).thenReturn(build);
        stubTargets(builder, fallbackBuilder);
        // when
        Response response = testee.getRepository("owner", "1");
        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getEntity()).isInstanceOf(GithubRepository.class);
        entity = GithubRepository.class.cast(response.getEntity());
        Assertions.assertThat(entity.getName()).isEqualTo(STUBBED_ENTITY_NAME);
        
        Mockito.verify(builder, Mockito.times(1)).get();
        Mockito.verify(fallbackBuilder, Mockito.never()).get();
    }
    
    @Test
    public void shouldThrowUnhealthyExceptionWhenNeitherOriginalNorFallbackAnswers() {
        //given
        Builder builder = Mockito.mock(Builder.class);
        Builder fallbackBuilder = Mockito.mock(Builder.class);
       
        Mockito.when(builder.get()).thenThrow(processingException);
        Mockito.when(fallbackBuilder.get()).thenThrow(processingException);
        
        stubTargets(builder, fallbackBuilder);         
        // when
        // then
        Assertions.assertThatExceptionOfType(ExternalServiceUnhealthy.class)
                .isThrownBy(() -> testee.getRepository("owner", "1"));
        
        Mockito.verify(builder, Mockito.times(RETRIES_COUNT)).get();
        Mockito.verify(fallbackBuilder, Mockito.times(RETRIES_COUNT)).get();
    }
    
    private void stubTargets(Builder originalInvocationBuilder, Builder fallbackInvocationBuilder) {
        Mockito.when(mockedWebTarget.resolveTemplate(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedWebTarget);
        Mockito.when(mockedWebTarget.request(MediaType.APPLICATION_JSON)).thenReturn(originalInvocationBuilder);
        Mockito.when(mockedFallbackTarget.resolveTemplate(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedFallbackTarget);
        Mockito.when(mockedFallbackTarget.request(MediaType.APPLICATION_JSON)).thenReturn(fallbackInvocationBuilder);
    }
}
