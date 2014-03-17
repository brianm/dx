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
