    public List<SinonimoDto> recuperaSinonimi(SinonimiServiceIn input) {
        List<SinonimoDto> response = new ArrayList<>();
        this.checkInput(input);
        String ricConferma;
        switch (input.getParmRicConferma()){
            case 'C':
                ricConferma = " ";
                break;
            case 'D':
                ricConferma = "SS";
                break;
            default:
                ricConferma = "S";
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint)
                .queryParam("institute", input.getIstituto())
                .queryParam("confirmationRequest", ricConferma)
                .queryParam("companyName", input.getRagioneSociale());
        if(StringUtils.isNotBlank(input.getCap())
                && input.getCap().matches("[0-9]+")
                && Integer.parseInt(input.getCap()) != 0)
            builder.queryParam("addressPostCode", input.getCap());
        if(StringUtils.isNotBlank(input.getLocalita()))
            builder.queryParam("addressLocal", input.getLocalita());
        if(StringUtils.isNotBlank(input.getIndirizzo()))
            builder.queryParam("addressStreet", input.getIndirizzo());
        log.debug("QueryString: {}", builder.toUriString());

        SinonimoDto[] sinonimi = null;

        final Timer.Context context = timer.time();
        try {
            sinonimi = restTemplate.getForObject(builder.build().toUri(), SinonimoDto[].class);
        } catch (Exception e){
            log.error("Error:", e);
            LogComposer
                    .using(log, zlog)
                    .add("An error occurred while invoking the Rest Service.")
                    .add(String.format("Parameters: %s", input))
                    .add(String.format("Query String: %s", builder.toUriString()))
                    .add(String.format("Error: %s", e.getMessage()))
                    .print();
//            throw new TechnicalException("Rest Service invocation Failed");
        } finally {
            this.counter.inc(TimeUnit.MILLISECONDS.convert(context.stop(), TimeUnit.NANOSECONDS));
        }
        if (sinonimi != null)
            response = Arrays.asList(sinonimi);
        if(log.isDebugEnabled()) {
            log.debug("Sinonimi Recuperati: {}", response.size());
            response.forEach(res -> log.debug("\t{}", res));
        }
        return response;
    }