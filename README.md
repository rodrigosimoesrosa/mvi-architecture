MVI Architecture with Jetpack Compose on Android
================================================

Introduction
============

MVI (Model-View-Intent) is a modern architectural design pattern for Android app development. In recent years, it has gained vast popularity as a more robust, scalable, and maintainable approach than traditional patterns like MVP (Model-View-Presenter) and MVC (Model-View-Controller).

The MVI pattern is based on functional reactive programming concepts and follows a unidirectional data flow pattern, which ensures a predictable and comprehensible state management process. This not only simplifies debugging but also promotes cleaner and testable code.

![captionless image](https://miro.medium.com/v2/resize:fit:1400/format:webp/1*R3yw2250nEuL15f4sZL6_w.png)

Implementation
==============

This MVI implementation was made using examples and after many reading articles to abstract ideas from the open-source community. My journey began with thoroughly exploring examples, examining how others addressed state manipulation through ViewModel, and navigating various edge cases. The outcome reflects countless hours of dedicated study and effort.

![captionless image](https://miro.medium.com/v2/resize:fit:1400/format:webp/1*XH3NxFfI5VkXJCe-zOJmaw.png)

MVI View @composable
--------------------

*   Users can interact with a view. We call this interaction by **_Intent._**
*   View reacts according to the State or Side Effect received.

MVI View Model
--------------

I designed a custom [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) to implement the MVI architecture effectively. This ViewModel has the specific purpose to orchestrate and manage core components of the MVI pattern, including state management, intent processing, event handling, and effect delivery, ensuring a seamless and structured flow within the application.

```
abstract class MVIViewModel<STATE : UiState, EVENT : UiEvent, EFFECT : UiEffect, INTENT : UiIntent> : ViewModel() {
    protected abstract fun getInitial(): STATE
    protected abstract fun onIntent(intent: INTENT)
    protected abstract fun onReduce(oldState: STATE, event: EVENT): STATE
    private val reducer: MVIReducer<STATE, EVENT> by lazy {
        object : MVIReducer<STATE, EVENT>(getInitial()) {
            override fun reduce(oldState: STATE, event: EVENT) {
                setState(this@MVIViewModel.onReduce(oldState, event))
            }
        }
    }
    val state: StateFlow<STATE>
        get() = reducer.state
    private val _events = Channel<EVENT>(Channel.BUFFERED)
    private val events = _events.receiveAsFlow()
    private val _intents: MutableSharedFlow<INTENT> = MutableSharedFlow()
    private val intents = _intents.asSharedFlow()
    private val _effects: MutableSharedFlow<EFFECT> = MutableSharedFlow()
    val effects = _effects.asSharedFlow()
    init {
        subscribeIntents()
        subscribeEvents()
        state.onEach { logInfo("State: $it") }.launchIn(viewModelScope)
        intents.onEach { logInfo("Intent: $it") }.launchIn(viewModelScope)
        effects.onEach { logInfo("Effect: $it") }.launchIn(viewModelScope)
    }
    private fun subscribeEvents() {
        viewModelScope.launch {
            events.collect { reducer.sendEvent(it) }
        }
    }
    private fun subscribeIntents() {
        viewModelScope.launch {
            intents.collect { onIntent(it) }
        }
    }
    fun setEvent(builder: () -> EVENT) {
        val event = builder()
        viewModelScope.launch {
            val delivered = _events.trySend(event).isSuccess
            if (!delivered) {
                error("Missed event $event!")
            }
        }
    }
    protected fun setEffect(builder: () -> EFFECT) {
        val effect = builder()
        viewModelScope.launch { _effects.emit(effect) }
    }
    fun setIntent(builder: () -> INTENT) {
        val intent = builder()
        viewModelScope.launch { _intents.emit(intent) }
    }
}
```

MVI Reducer
-----------

This component is responsible for controlling states using events:

```
abstract class MVIReducer<STATE : UiState, EVENT : UiEvent>(state: STATE) {
    private val _state: MutableStateFlow<STATE> = MutableStateFlow(state)
    val state: StateFlow<STATE>
        get() = _state
    fun sendEvent(event: EVENT) { reduce(_state.value, event) }
    fun setState(newState: STATE) {
        _state.tryEmit(newState)
    }
    abstract fun reduce(oldState: STATE, event: EVENT)
}
```

MVI Interfaces
--------------

```
interface UiEffect
interface UiEvent
interface UiIntent
interface UiState
```

MVI + Clean Architecture
========================

This MVI implementation was made to comply with the Clean Architecture structure:

![captionless image](https://miro.medium.com/v2/resize:fit:1400/format:webp/1*bMyO-L4u_NehE_0iEMrmzg.png)

Example
=======

Let’s walk through an example using a commonly implemented screen: **Sign-In**.

SignInViewModel:
----------------

```
@HiltViewModel
class SignInViewModel @Inject constructor(private val useCase: UserUseCase.SignInOut) :
    MVIViewModel<SignIn.State, SignIn.Event, SignIn.Effect, SignIn.Intent>() {
    override fun getInitial(): SignIn.State = SignIn.State.initial()
    override fun onIntent(intent: SignIn.Intent) {
        when (intent) {
            is SignIn.Intent.Submit -> submit()
            is SignIn.Intent.Back -> setEffect { SignIn.Effect.OnBack }
            is SignIn.Intent.ForgotPassword -> setEffect { SignIn.Effect.OnForgotPassword }
            is SignIn.Intent.OnTryAgain -> setEvent { SignIn.Event.TryAgain }
        }
    }
    override fun onReduce(oldState: SignIn.State, event: SignIn.Event): SignIn.State {
        return when (event) {
            is SignIn.Event.UpdateEmail -> oldState.copy(email = event.email, invalidEmail = false)
            is SignIn.Event.InvalidEmail -> oldState.copy(invalidEmail = true)
            is SignIn.Event.UpdatePassword -> oldState.copy(
                password = event.password,
                invalidPassword = false
            )
            is SignIn.Event.InvalidPassword -> oldState.copy(invalidPassword = true)
            is SignIn.Event.Loading -> oldState.copy(isLoading = true)
            is SignIn.Event.Success -> {
                setEffect { SignIn.Effect.ShowSuccess }
                oldState.copy(isLoading = false)
            }
            is SignIn.Event.Error -> {
                oldState.copy(isLoading = false, unsuccessful = true)
            }
            is SignIn.Event.TryAgain -> oldState.copy(
                invalidEmail = false,
                invalidPassword = false,
                isLoading = false,
                unsuccessful = false
            )
        }
    }
    private fun submit() {
        viewModelScope.launch {
            if (Validator.Email.isInvalid(state.value.email)) {
                setEvent { SignIn.Event.InvalidEmail }
                return@launch
            }
            if (Validator.Password.isInvalid(state.value.password)) {
                setEvent { SignIn.Event.InvalidPassword }
                return@launch
            }
            setEvent { SignIn.Event.Loading }
            when (val result = useCase.signIn(state.value.email, state.value.password)) {
                is Result.Success -> setEvent { SignIn.Event.Success }
                is Result.Error -> setEvent { SignIn.Event.Error(result.error) }
            }
        }
    }
}
```

SignIn object:
--------------

```
import javax.annotation.concurrent.Immutable
import UiEffect
import UiEvent
import UiIntent
import UiState
object SignIn {
    @Immutable
    sealed interface Intent : UiIntent {
        data object Submit : Intent
        data object ForgotPassword : Intent
        data object Back : Intent
        data object OnTryAgain : Intent
    }
    @Immutable
    sealed interface Event : UiEvent {
        data object Success : Event
        data class UpdateEmail(val email: String) : Event
        data object InvalidEmail : Event
        data class UpdatePassword(val password: String) : Event
        data object InvalidPassword : Event
        data object Loading : Event
        data class Error(val error: BaseError) : Event
        data object TryAgain : Event
    }
    @Immutable
    data class State(
        val email: String,
        val invalidEmail: Boolean,
        val password: String,
        val invalidPassword: Boolean,
        val isLoading: Boolean,
        val unsuccessful: Boolean
    ) : UiState {
        fun canSubmit(): Boolean {
            if (isLoading) return false
            
            if (Validator.Email.isEmpty(email) ||
                Validator.Password.isEmpty(password)
            ) return false
            return !invalidEmail && !invalidPassword
        }
        companion object {
            fun initial() = State(
                email = "",
                invalidEmail = false,
                password = "",
                invalidPassword = false,
                isLoading = false,
                unsuccessful = false
            )
            fun progress() = State(
                email = "email@email.com",
                invalidEmail = false,
                password = "qwerty",
                invalidPassword = false,
                isLoading = true,
                unsuccessful = false
            )
            fun invalidFields() = State(
                email = "email",
                invalidEmail = true,
                password = "qwer",
                invalidPassword = true,
                isLoading = false,
                unsuccessful = false
            )
            fun loginUnsuccessful() = State(
                email = "email",
                invalidEmail = false,
                password = "qwer",
                invalidPassword = false,
                isLoading = false,
                unsuccessful = true
            )
        }
    }
    @Immutable
    sealed interface Effect : UiEffect {
        data object OnShowSuccess : Effect
        data object OnBack : Effect
        data object OnForgotPassword : Effect
    }
}
```

SignIn screen composable:
-------------------------

```
@Composable
fun SignInScreen(
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val viewModel: SignInViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is SignIn.Effect.ShowSuccess -> onSuccess()
                is SignIn.Effect.OnBack -> onBack()
                is SignIn.Effect.OnForgotPassword -> onForgotPassword()
            }
        }
    }
    SignInContent(
        onState = { state },
        onEvent = { event -> viewModel.setEvent { event } },
        onIntent = { intent -> viewModel.setIntent { intent } }
    )
}
@Composable
fun SignInContent(
    onState: () -> SignIn.State,
    onEvent: (SignIn.Event) -> Unit,
    onIntent: (SignIn.Intent) -> Unit
) {
    if (onState().isLoading) {
        LoadingDialog()
    }
    if (onState().unsuccessful) {
        UnsuccessfulDialog()
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            MyToolbar(
                onBackPressed = {
                    onIntent(SignIn.Intent.Back)
                }
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            PageInfo()
            MInputTextField(
                modifier = Modifier
                    .padding(PaddingValues(top = 8.dp))
                    .fillMaxWidth(1f)
                    .align(Alignment.CenterHorizontally),
                onValueChange = { onEvent(SignIn.Event.UpdateEmail(it)) },
                value = onState().email,
                errorMessage = if (onState().invalidEmail) "Invalid Email" else "",
                label = "Email",
                onKeyboardOptions = { KeyboardOptions(imeAction = ImeAction.Next) }
            )
            MyInputTextField(
                modifier = Modifier
                    .padding(PaddingValues(top = 8.dp))
                    .fillMaxWidth(1f)
                    .align(Alignment.CenterHorizontally),
                isPassword = true,
                value = onState().password,
                errorMessage = if (onState().invalidPassword) "Invalid Password" else "",
                label = "Password",
                onValueChange = { onEvent(SignIn.Event.UpdatePassword(it)) },
                onKeyboardOptions = { KeyboardOptions(imeAction = ImeAction.Done) },
                onSubmit = { onIntent(SignIn.Intent.Submit) }
            )
            MyButton(
                text = "Login",
                modifier = Modifier
                    .padding(PaddingValues(top = 16.dp))
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally),
                enabled = onState().canSubmit(),
                onClick = { onIntent(SignIn.Intent.Submit) }
            )
            MyTextButton(
                text = "Forgot Password",
                modifier = Modifier
                    .padding(PaddingValues(top = 8.dp))
                    .align(Alignment.CenterHorizontally),
                onClick = { onIntent(SignIn.Intent.ForgotPassword) }
            )
        }
    }
}
@Preview
@Composable
fun SignInScreenPreview(state: SignIn.State = SignIn.State.initial()) {
    AppTheme {
        SignInContent({ state }, {}) {}
    }
}
```

Reference
=========

[1] [Clean Architecture: A Craftsman’s Guide to Software Structure and Design (Robert C. Martin Series)](https://www.amazon.com/Clean-Architecture-Craftsmans-Software-Structure/dp/0134494164)

[2] [https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

[3] [https://developer.android.com/modern-android-development](https://developer.android.com/modern-android-development)

[4] [https://medium.com/green-code-innovations/the-transition-from-mvvm-to-mvi-for-cleaner-predictable-code-9244c3aa488d](https://medium.com/green-code-innovations/the-transition-from-mvvm-to-mvi-for-cleaner-predictable-code-9244c3aa488d)

[5] [https://dashwave.io/blog/android-architecture-patterns/](https://dashwave.io/blog/android-architecture-patterns/)

[6] [https://betterprogramming.pub/all-you-need-for-mvi-is-kotlin-how-to-reduce-without-reducer-5e986856610f](https://betterprogramming.pub/all-you-need-for-mvi-is-kotlin-how-to-reduce-without-reducer-5e986856610f)

[7] [https://www.kodeco.com/817602-mvi-architecture-for-android-tutorial-getting-started](https://www.kodeco.com/817602-mvi-architecture-for-android-tutorial-getting-started)

[8] [https://betterprogramming.pub/introducing-decoupled-mvi-approach-for-android-in-2023-b93e4a16fb1b](https://betterprogramming.pub/introducing-decoupled-mvi-approach-for-android-in-2023-b93e4a16fb1b)

[9] [https://proandroiddev.com/mvi-architecture-with-kotlin-flows-and-channels-d36820b2028d](https://proandroiddev.com/mvi-architecture-with-kotlin-flows-and-channels-d36820b2028d)

[10] [https://dev.to/kaleidot725/implementaing-jetpack-compose-orbit-mvi-3gea](https://dev.to/kaleidot725/implementaing-jetpack-compose-orbit-mvi-3gea)
