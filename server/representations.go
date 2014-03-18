package server

type Link struct {
	Href string `json:"href"`
}

type Service struct {
	Links   map[string]Link `json:"_links,omitempty"`
	Type    string          `json:"type"`
	Url     string          `json:"url"`
	Pool    string          `json:"pool"`
	Version string          `json:"version"`
}

/*
func (s Service) String() string {
	j, err := json.Marshal(s)
	if err != nil {
		return err.Error()
	}
	return string(j)
}
*/
